package com.chess.puzzle.text2sql.web.service.llm

import io.ktor.client.statement.HttpResponse

interface LargeLanguageModel {
    suspend fun callModel(query: String): HttpResponse
}
