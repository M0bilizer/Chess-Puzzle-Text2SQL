
# Sentence Transformer Microservice

This is a FastAPI application developed to assist the main application in finding similar demonstration for the Text2SQL process. This takes inspiration from Gao et al. (2023)'s paper in where they performed schema-masking to hide database keywords to find more relevant demonstrations.

> Gao, D., Wang, H., Li, Y., Sun, X., Qian, Y., Ding, B., & Zhou, J. (2023). Text-to-sql empowered by large language models: A benchmark evaluation. arXiv preprint arXiv:2308.15363.

----
## Prerequisite

This project uses `uv`, an extremely fast Python package and project manager, written in Rust. `uv` helped in reducing the download time from 15min to 5min!

These are the system requirement

- Python 3.9: Ensure Python 3.9 is installed on your system.

- Docker (optional): If you want to run the application in a Docker container.

---
## Installation

1. If you don’t have `uv` installed, you can install it using `pip`:
```commandline
pip install uv
```

2. Install Dependencies
```commandline
uv sync
```

3. Activate Virtual Environemt

For Windows:
```commandline
.venv\Scripts\activate
```

For macOS and Linux:
```commandline
source .venv/bin/activate
```

---
## Running the application

### Locally

- Development

Run the FastAPI application in development using uvicorn:
```commandline
uvicorn src.main:app --reload
```
The application will be available at http://127.0.0.1:8000.


- Production

Run the FastAPI application in production using uvicorn:
```commandline
uvicorn src.main:app --host 0.0.0.0 --port 8000
```
### Using Docker

If you prefer to run the application in a Docker container, follow these steps:

1. Build the Docker Image:
```commandline
docker build -t my-fastapi-app .
```

2. Run the Docker Container:
```commandline
docker run -p 8000:8000 my-fastapi-app
```

The application will be available at http://127.0.0.1:8000.

---
## Other useful commands

- Lint the project using `ruff`:
```commandline
ruff format
```

- Build the docker image with *python-fastapi* as it's name:
```commandline
bash build.sh
```

---
## Modules Used

| Module               | Description / Purpose                              |
|----------------------|----------------------------------------------------|
| Sentence Transformer | Find similar demonstration, perform schema masking |
| fastapi              | Backend                                            |
| uvicorn              | Server for Fastapi                                 |
| commentjson          | Remove comments from JSON files                    |
