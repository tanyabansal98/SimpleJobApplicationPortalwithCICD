import glob
import json
import os

import psycopg2

EMPLOYER_USER_ID = int(os.environ["EMPLOYER_USER_ID"])

UPSERT_SQL = """
INSERT INTO jobs (employer_user_id, title, description, location, company_name,
                  job_type, is_active, created_at, external_job_id, source_url)
VALUES (%s, %s, %s, %s, %s, %s, TRUE, NOW(), %s, %s)
ON CONFLICT (external_job_id) DO UPDATE SET
    title        = EXCLUDED.title,
    description  = EXCLUDED.description,
    location     = EXCLUDED.location,
    company_name = EXCLUDED.company_name,
    source_url   = EXCLUDED.source_url
RETURNING (xmax = 0)
"""


def latest_json():
    files = sorted(glob.glob("job_details_*.json"))
    if not files:
        raise FileNotFoundError("No job_details_*.json file in this folder.")
    return files[-1]


def job_type(title):
    t = title.lower()
    if "co-op" in t or "coop" in t:
        return "Co-op"
    if "intern" in t:
        return "Internship"
    return None


def clip(value, limit):
    return value[:limit] if value else value


def connect():
    return psycopg2.connect(
        host=os.getenv("PGHOST", "localhost"),
        port=os.getenv("PGPORT", "5432"),
        dbname=os.getenv("PGDATABASE", "jobportal"),
        user=os.getenv("PGUSER", "postgres"),
        password=os.environ["PGPASSWORD"],
    )


if __name__ == "__main__":
    path = latest_json()
    with open(path, encoding="utf-8") as f:
        jobs = json.load(f)
    print(f"Loading {len(jobs)} jobs from {path}")

    inserted = updated = skipped = 0
    conn = connect()
    try:
        with conn:                        # one transaction: commit on success, rollback on error
            with conn.cursor() as cur:
                for j in jobs:
                    if not j.get("job_id") or not j.get("position"):
                        print(f"Skipping (missing id or title): {j.get('url')}")
                        skipped += 1
                        continue
                    cur.execute(UPSERT_SQL, (
                        EMPLOYER_USER_ID,
                        clip(j["position"], 200),
                        j.get("description"),
                        clip(j.get("location"), 200),
                        clip(j.get("company"), 200),
                        job_type(j["position"]),
                        j["job_id"],
                        j.get("url"),
                    ))
                    if cur.fetchone()[0]:
                        inserted += 1
                    else:
                        updated += 1
    finally:
        conn.close()

    print(f"Inserted {inserted}, updated {updated}, skipped {skipped}")