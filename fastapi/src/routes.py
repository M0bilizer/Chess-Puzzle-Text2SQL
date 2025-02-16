import torch
from fastapi import APIRouter
from scipy.spatial.distance import cosine

from src.models import Request, ResponseDto
from src.config import MODEL
from src.utils import (
    mask_keywords,
    tokenize,
    jaccard_similarity,
    demo_texts,
    demo_embedding,
    demonstrations,
)

api_router = APIRouter()


@api_router.get("/api")
async def hello():
    return "Hello from fastapi!"


@api_router.post("/api/similarity", response_model=ResponseDto)
async def mask_and_find_similar(request: Request):
    input_text = request.query

    masked_query = mask_keywords(input_text)
    masked_tokens = tokenize(masked_query)

    tokenized_demonstrations = [demo for demo in demo_texts]

    jaccard_similarities = [
        jaccard_similarity(masked_tokens, demo) for demo in tokenized_demonstrations
    ]

    top_k_indices = torch.tensor(jaccard_similarities).topk(3).indices.tolist()
    similar_demonstrations = [demonstrations[i] for i in top_k_indices]

    return ResponseDto(
        status="success", masked_query=masked_query, data=similar_demonstrations
    )


@api_router.post("/api/similarity/partial", response_model=ResponseDto)
async def find_similar(request: Request):
    input_text = request.query

    text_embedding = MODEL.encode(input_text, convert_to_tensor=True)

    cosine_similarities = [cosine(text_embedding, demo) for demo in demo_embedding]

    top_k_indices = torch.tensor(cosine_similarities).topk(3).indices.tolist()
    similar_demonstrations = [demonstrations[i] for i in top_k_indices]

    return ResponseDto(
        status="success", masked_query="<DISABLED>", data=similar_demonstrations
    )
