package com.chess.puzzle.text2sql.web.domain.model.llm

import io.ktor.client.HttpClient

data class OpenAiClient(val client: HttpClient, val apiKey: String, val baseUrl: String)
