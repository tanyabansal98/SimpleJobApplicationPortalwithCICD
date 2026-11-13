import hashlib
import os

import chromadb
import psycopg2
from dotenv import load_dotenv
from openai import OpenAI
from logger_setup import get_logger

log = get_logger("embed_jobs")
load_dotenv()


COLLECTION = os.getenv("CHROMA_COLLECTION", "job-embeddings")
EMBED_MODEL = "text-embedding-3-small"
CHUNK_WORDS = 200
OVERLAP_WORDS = 40
ONLY_SCRAPED = False   # True: only jobs that have an external_id (scraped jobs)


def connect_pg():
    return psycopg2.connect(
        host=os.getenv("PGHOST", "localhost"),
        port=os.getenv("PGPORT", "5432"),
        dbname=os.getenv("PGDATABASE", "jobportal"),
        user=os.getenv("PGUSER", "postgres"),
        password=os.environ["PGPASSWORD"],
    )


def fetch_jobs(cur):
    sql = """SELECT job_id, title, company_name, location, description, job_type, source_url
             FROM jobs WHERE is_active = TRUE"""
    if ONLY_SCRAPED:
        sql += " AND external_id IS NOT NULL"
    cur.execute(sql) # A cursor (cur) is the object that runs SQL
    rows = cur.fetchall()
    log.info("Fetched %d active jobs from Postgres (only_scraped=%s)", len(rows), ONLY_SCRAPED)
    cols = ["job_id", "title", "company", "location", "description", "job_type", "source_url"]
    return [dict(zip(cols, row)) for row in cur.fetchall()]


def content_hash(job):
    raw = "|".join(str(job[k] or "") for k in ("title", "company", "location", "description"))
    return hashlib.sha1(raw.encode("utf-8")).hexdigest()


def chunk_words(text, size, overlap):
    words = (text or "").split()
    if not words:
        return []
    step = size - overlap
    chunks = []
    for start in range(0, len(words), step):
        chunks.append(" ".join(words[start:start + size]))
        if start + size >= len(words):
            break
    return chunks


def build_chunks(job):
    header = f"{job['title']} at {job['company'] or 'Unknown company'}, {job['location'] or 'Unknown location'}"
    bodies = chunk_words(job["description"], CHUNK_WORDS, OVERLAP_WORDS) or [""]
    return [f"{header}\n\n{body}".strip() for body in bodies]


def stored_hashes(collection):
    result = collection.get(include=["metadatas"])
    hashes = {}
    for meta in result["metadatas"]:
        if meta and "job_id" in meta:
            hashes[meta["job_id"]] = meta.get("content_hash")
    return hashes


def embed(client, texts):
    response = client.embeddings.create(model=EMBED_MODEL, input=texts)
    return [item.embedding for item in response.data]


if __name__ == "__main__":
    log.info("Run started: collection=%s, model=%s, chunk=%d words, overlap=%d",
             COLLECTION, EMBED_MODEL, CHUNK_WORDS, OVERLAP_WORDS)

    openai_client = OpenAI()   # reads OPENAI_API_KEY from the environment
    chroma = chromadb.HttpClient(host=os.getenv("CHROMA_HOST", "localhost"),
                                 port=int(os.getenv("CHROMA_PORT", "8000")))
    collection = chroma.get_or_create_collection(name=COLLECTION)
    log.info("Connected to ChromaDB, collection '%s' has %d chunks", COLLECTION, collection.count())

    conn = connect_pg()
    try:
        with conn.cursor() as cur:
            jobs = fetch_jobs(cur)
    finally:
        conn.close()

    known = stored_hashes(collection)
    todo = [j for j in jobs if known.get(j["job_id"]) != content_hash(j)]
    #print(f"{len(jobs)} jobs in Postgres, {len(todo)} new or changed")
    log.info("%d jobs in Postgres, %d new or changed", len(jobs), len(todo))

    done = failed = 0
    for job in todo:
        try:
            chunks = build_chunks(job)
            vectors = embed(openai_client, chunks)
            h = content_hash(job)
            collection.delete(where={"job_id": job["job_id"]})   # remove old chunks of this job
            collection.upsert(
                ids=[f"job-{job['job_id']}-chunk-{i}" for i in range(len(chunks))],
                embeddings=vectors,
                documents=chunks,
                metadatas=[{
                    "job_id": job["job_id"],
                    "title": job["title"] or "",
                    "company": job["company"] or "",
                    "location": job["location"] or "",
                    "job_type": job["job_type"] or "",
                    "source_url": job["source_url"] or "",
                    "chunk_index": i,
                    "content_hash": h,
                } for i in range(len(chunks))],
            )
            done += 1
            #print(f"OK job {job['job_id']}: {job['title']} ({len(chunks)} chunks)")
            log.info("OK job %s: %s (%d chunks)", job["job_id"], job["title"], len(chunks))
        except Exception as e:
            failed += 1
            #print(f"FAILED job {job['job_id']}: {e}")
            log.exception("FAILED job %s", job["job_id"])

    #print(f"Embedded {done} jobs, {failed} failed. Collection now has {collection.count()} chunks.")
    log.info("Embedded %d jobs, %d failed. Collection now has %d chunks.",
             done, failed, collection.count())