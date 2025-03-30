package com.chess.puzzle.text2sql.web.domain.model.llm

import io.ktor.client.HttpClient

/**
 * Represents the OpenAI client configuration, encapsulating the necessary components to interact
 * with the OpenAI API.
 *
 * This class configures how the OpenAI client communicates with the API, including HTTP client
 * configuration, authentication (API key), and the base URL for API requests.
 *
 * @property client The HTTP client used to make requests to the OpenAI API.
 * @property apiKey The API key used for authentication with the OpenAI API.
 * @property baseUrl The base URL for the OpenAI API endpoint (e.g., "https://api.openai.com/v1").
 */
data class OpenAiClient(val client: HttpClient, val apiKey: String, val baseUrl: String)
