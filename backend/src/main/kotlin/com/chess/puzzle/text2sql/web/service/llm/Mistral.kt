package com.chess.puzzle.text2sql.web.service.llm

import com.chess.puzzle.text2sql.web.domain.model.llm.ChatCompletionRequest
import com.chess.puzzle.text2sql.web.domain.model.llm.Message
import com.chess.puzzle.text2sql.web.domain.model.llm.OpenAiClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class Mistral(@Qualifier("mistralClient") private val client: OpenAiClient) : LargeLanguageModel {
    override suspend fun callModel(query: String): HttpResponse {
        val (client, apiKey, baseUrl) = client

        val requestBody =
            ChatCompletionRequest(
                model = "mistral-small-latest",
                messages = listOf(Message(role = "user", content = query)),
                stream = false,
            )

        val response: HttpResponse =
            client.post(baseUrl) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $apiKey")
                setBody(requestBody)
            }

        return response
    }
}
