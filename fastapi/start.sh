#!/bin/bash

pip install torch==2.4.1
pip install fastapi
pip install fastapi[standard]
pip install pydantic
pip install sentence-transformers
pip install commentjson
uvicorn src.main:app --reload