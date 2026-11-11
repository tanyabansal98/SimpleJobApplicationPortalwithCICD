import chromadb

c = chromadb.HttpClient(host="localhost", port=8000).get_collection("job-embeddings")
print("count:", c.count())
print("metadata:", c.metadata)
sample = c.peek(2)
print("ids:", sample["ids"])
print("metadatas:", sample["metadatas"])
print("document:", (sample["documents"][0] or "")[:300])