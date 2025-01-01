from fastapi import FastAPI

from .routes import api_router

app = FastAPI()

app.include_router(api_router)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000)

# lint: ruff format
# dev: uvicorn src.main:app --reload
# prod: uvicorn src.main:app --host 0.0.0.0 --port 8000
