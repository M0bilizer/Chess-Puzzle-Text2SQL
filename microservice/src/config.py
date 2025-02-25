from sentence_transformers import SentenceTransformer

MODEL = SentenceTransformer("all-mpnet-base-v2")
KEYWORD_FILE_PATH = "src/data/keywords.json"
DEMONSTRATIONS_FILE_PATH = "src/data/demonstrations.json"
