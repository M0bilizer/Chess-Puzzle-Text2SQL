from typing import List

from pydantic import BaseModel


class Request(BaseModel):
    query: str


class ResponseDto(BaseModel):
    status: str
    masked_query: str
    data: List[dict]
