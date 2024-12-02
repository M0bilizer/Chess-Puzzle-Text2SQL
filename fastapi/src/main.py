from fastapi import FastAPI
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer, util
import json
import commentjson
from typing import List
import torch

app = FastAPI()

# Load the sentence transformer model
model = SentenceTransformer('all-mpnet-base-v2')


def ngrams(input_text, n):
    words = input_text.split()
    return [' '.join(words[i:i + n]) for i in range(len(words) - n + 1)]


def mask_keywords(input_text: str) -> str:
    """Masks keywords in the input text based on defined categories."""
    keyword_masks = {
        "themes": "<theme>",
        "opening_tags": "<opening_tag>"
    }
    max_ngram = 4
    threshold = 0.65

    cleaned = input_text.replace("'", "").lower()

    # Encode the input words
    ngrams_list = []
    for n in range(1, max_ngram + 1):
        ngrams_list.extend(ngrams(cleaned, n))

    # Encode the n-grams
    ngram_embeddings = model.encode(ngrams_list, convert_to_tensor=True)

    # Compare each n-gram with the keywords and mask if similar
    masked_ngrams = []
    for ngram, ngram_embedding in zip(ngrams_list, ngram_embeddings):
        for category, embeddings in keyword_embeddings.items():
            similarities = util.pytorch_cos_sim(ngram_embedding, embeddings)
            max_similarity = torch.max(similarities).item()
            if max_similarity >= threshold:
                masked_ngrams.append((ngram, keyword_masks.get(category, f"<{category}>")))
                break
        else:
            masked_ngrams.append((ngram, ngram))

    # Replace the original n-grams with the masked ones
    masked_text = cleaned
    for original, masked in reversed(masked_ngrams):
        masked_text = masked_text.replace(original, masked)

    return masked_text


def tokenize(sentence):
    return set(sentence.lower().split())


# Compute Jaccard similarity between two sets
def jaccard_similarity(set1, set2):
    intersection = set1.intersection(set2)
    union = set1.union(set2)
    return len(intersection) / len(union)

# Load keywords from JSON file
with open('src/keywords.json', 'r') as f:
    keywords_data = json.load(f)

keyword_embeddings = {category: model.encode(keywords, convert_to_tensor=True) for category, keywords in
                      keywords_data.items()}

# Load demonstrations from JSON file
with open('src/demonstrations.json', 'r') as f:
    demonstrations = commentjson.load(f)

demo_texts = [tokenize(mask_keywords(demo["text"])) for demo in demonstrations]


class Request(BaseModel):
    query: str


class ResponseDto(BaseModel):
    status: str
    data: List[dict]


@app.post("/api/similarity/", response_model=ResponseDto)
async def mask_and_find_similar(request: Request):
    input_text = request.query
    masked_text = mask_keywords(input_text)
    print(masked_text)

    masked_tokens = tokenize(masked_text)

    tokenized_demonstrations = [demo for demo in demo_texts]

    # Compute Jaccard similarity between the masked text and each demonstration
    jaccard_similarities = [jaccard_similarity(masked_tokens, demo) for demo in tokenized_demonstrations]

    # Find the top-k demonstrations with the highest Jaccard similarity
    top_k_indices = torch.tensor(jaccard_similarities).topk(3).indices.tolist()
    similar_demonstrations = [demonstrations[i] for i in top_k_indices]

    return ResponseDto(status="success", data=similar_demonstrations)

# Run your FastAPI src using uvicorn src.main:src --reload
