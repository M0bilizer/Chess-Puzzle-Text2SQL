import json
from typing import List

import commentjson
import torch
import re
from sentence_transformers import util

from src.config import MODEL, KEYWORD_FILE_PATH, DEMONSTRATIONS_FILE_PATH


def tokenize(sentence):
    cleaned_sentence = re.sub(r"[^\w\s]", "", sentence.lower())
    return set(cleaned_sentence.lower().split())


def ngrams(input_text: str, n) -> List[str]:
    words = input_text.split()
    return [" ".join(words[i : i + n]) for i in range(len(words) - n + 1)]


def mask_keywords(input_text: str) -> str:
    keyword_masks = {"themes": "<theme>", "opening_tags": "<opening_tag>"}
    max_ngram = 4
    threshold = 0.65

    cleaned = input_text.replace("'", "").lower()

    ngrams_list = []
    for n in range(1, max_ngram + 1):
        ngrams_list.extend(ngrams(cleaned, n))

    ngram_embeddings = MODEL.encode(ngrams_list, convert_to_tensor=True)

    masked_ngrams = []
    for ngram, ngram_embedding in zip(ngrams_list, ngram_embeddings):
        for category, embeddings in keyword_embeddings.items():
            similarities = util.pytorch_cos_sim(ngram_embedding, embeddings)
            max_similarity = torch.max(similarities).item()
            if max_similarity >= threshold:
                masked_ngrams.append(
                    (ngram, keyword_masks.get(category, f"<{category}>"))
                )
                break
        else:
            masked_ngrams.append((ngram, ngram))

    masked_text = cleaned
    for original, masked in reversed(masked_ngrams):
        masked_text = masked_text.replace(original, masked)

    return masked_text


def jaccard_similarity(set1, set2):
    intersection = set1.intersection(set2)
    union = set1.union(set2)
    return len(intersection) / len(union)


with open(KEYWORD_FILE_PATH, "r") as f:
    keywords_data = json.load(f)

with open(DEMONSTRATIONS_FILE_PATH, "r") as f:
    demonstrations = commentjson.load(f)

keyword_embeddings = {
    category: MODEL.encode(keywords, convert_to_tensor=True)
    for category, keywords in keywords_data.items()
}

demo_texts = [tokenize(mask_keywords(demo["text"])) for demo in demonstrations]
demo_embedding = [
    MODEL.encode(demo["text"], convert_to_tensor=True) for demo in demonstrations
]
