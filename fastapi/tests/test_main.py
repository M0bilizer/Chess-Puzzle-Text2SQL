from fastapi.testclient import TestClient
from main import app

client = TestClient(app)


def test_hello():
    response = client.get("/api")
    assert response.status_code == 200
    assert response.json() == "Hello from fastapi!"


def test_invalid_route():
    response = client.get("/invalid-route")
    assert response.status_code == 404
    assert response.json() == {"detail": "Not Found"}


def test_router_inclusion():
    response = client.post("/api/similarity", json={"query": "test"})
    assert response.status_code == 200
    assert "status" in response.json()
    assert "masked_query" in response.json()
    assert "data" in response.json()
