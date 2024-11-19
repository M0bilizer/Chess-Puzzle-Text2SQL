from fastapi import FastAPI
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer, util
import json
import commentjson
from typing import List
import os

app = FastAPI()

# Load the sentence transformer model
model = SentenceTransformer('all-mpnet-base-v2')

# Load keywords from JSON file
with open('src/keywords.json', 'r') as f:
    keywords_data = json.load(f)

# Load demonstrations from JSON file
with open('src/demonstrations.json', 'r') as f:
    demonstrations = commentjson.load(f)

demo_texts = [demo["text"] for demo in demonstrations]
demo_embeddings = model.encode(demo_texts, convert_to_tensor=True)


class Request(BaseModel):
    query: str


class Response(BaseModel):
    similar_demonstrations: List[dict]


def mask_keywords(input_text: str) -> str:
    """Masks keywords in the input text based on defined categories."""
    keyword_masks = {
        "animals": "<animal>",
        "locations": "<location>"
    }

    masked_text = input_text

    # Mask the input text for each category
    for category, keywords in keywords_data.items():
        mask = keyword_masks.get(category, f"<{category}>")
        for keyword in keywords:
            masked_text = masked_text.replace(keyword, mask)

    return masked_text


@app.post("/api/similarity/", response_model=Response)
async def mask_and_find_similar(request: Request):
    input_text = request.query
    masked_text = mask_keywords(input_text)
    print(masked_text)
    masked_embedding = model.encode(masked_text, convert_to_tensor=True)

    demo_similarities = util.pytorch_cos_sim(masked_embedding, demo_embeddings)
    top_k_indices = demo_similarities[0].topk(3).indices.tolist()
    similar_demonstrations = [demonstrations[i] for i in top_k_indices]

    return Response(similar_demonstrations=similar_demonstrations)

# Run your FastAPI app using uvicorn src.main:app --reload
