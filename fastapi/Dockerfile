FROM ghcr.io/astral-sh/uv:python3.9-bookworm

WORKDIR /app

COPY src /app/src
COPY start.sh /app/start.sh

RUN apt-get update && apt-get -y install curl

RUN chmod +x /app/start.sh

EXPOSE 8000
CMD ["bash","/app/start.sh"]
