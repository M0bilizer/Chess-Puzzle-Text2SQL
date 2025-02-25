from sentence_transformers import SentenceTransformer
from src.config import MODEL


def test_model_type():
    assert isinstance(MODEL, SentenceTransformer)
