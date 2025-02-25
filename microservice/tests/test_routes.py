from fastapi.testclient import TestClient

from src.routes import api_router

client = TestClient(api_router)


def test_hello():
    response = client.get("/api")
    assert response.status_code == 200
    assert response.json() == "Hello from fastapi!"


def test_similarity_endpoint():
    test_input = {"query": "This is a test query"}
    expected_output_keys = {"status", "masked_query", "data"}

    response = client.post("/api/similarity", json=test_input)

    assert response.status_code == 200
    assert all(key in response.json() for key in expected_output_keys)


def test_similarity_partial_endpoint():
    test_input = {"query": "This is another test query"}
    expected_output_keys = {"status", "masked_query", "data"}

    response = client.post("/api/similarity/partial", json=test_input)

    assert response.status_code == 200
    assert all(key in response.json() for key in expected_output_keys)
