from fastapi import FastAPI

from src.routes import api_router

app = FastAPI()

app.include_router(api_router)

if __name__ == "__main__":
    import uvicorn

    uvicorn.run("main:app", host="0.0.0.0", port=8000)

# get ruff using: uv tool install ruff@latest
# lint: ruff format
# dev: uvicorn main:app --reload
# prod: uvicorn main:app --host 0.0.0.0 --port 8000
