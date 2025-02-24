FROM python:3.9-slim

WORKDIR /app

ENV UV_HTTP_TIMEOUT=300

COPY src /app/src
COPY start.sh /app/start.sh
COPY main.py /app/main.py
COPY requirements.txt /app/requirements.txt

RUN apt-get update && apt-get -y install curl

RUN chmod +x /app/start.sh

EXPOSE 8000

CMD ["bash", "/app/start.sh"]