FROM ghcr.io/astral-sh/uv:python3.9-bookworm-slim

WORKDIR /app

ENV UV_HTTP_TIMEOUT=300
ENV UV_SYSTEM_PYTHON=1

COPY requirements.txt /app/requirements.txt
RUN apt-get update && apt-get -y install curl

COPY src /app/src
COPY main.py /app/main.py
COPY start.sh /app/start.sh

EXPOSE 8000

CMD ["bash", "start.sh"]
