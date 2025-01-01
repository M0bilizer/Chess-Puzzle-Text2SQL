#!/bin/bash

pip install uv
uv venv venv
source venv/bin/activate
uv pip install -r /app/requirements.txt
uvicorn src.main:app --host 0.0.0.0 --port 8000