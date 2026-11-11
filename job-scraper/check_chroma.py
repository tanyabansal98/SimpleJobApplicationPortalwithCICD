import chromadb

client = chromadb.HttpClient(host="localhost", port=8000)
col = client.get_collection("job-embeddings")
print("Total chunks:", col.count())

data = col.get(include=["metadatas"])   # no limit = all items

# Group chunks by job: one entry per job_id
jobs = {}
for m in data["metadatas"]:
    jobs[m["job_id"]] = m

print("Distinct jobs:", len(jobs))
print()
for job_id in sorted(jobs):
    m = jobs[job_id]
    print(job_id, "|", m.get("title"), "|", m.get("company"), "|", m.get("location"))