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

/**
 * A Deepseek large language model implementation of the [LargeLanguageModel] interface.
 *
 * This class is responsible for interacting with the Deepseek API to generate text completions
 * based on user-provided queries.
 *
 * @property client The qualified OpenAI client instance configured for Deepseek services.
 */
@Component
class Deepseek(@Qualifier("deepSeekClient") private val client: OpenAiClient) : LargeLanguageModel {

    /**
     * Calls the Deepseek API to generate a completion response based on the provided query.
     *
     * This function constructs a request to the Deepseek API, sends it, and handles the response.
     * It uses the provided query to create a text completion request and returns the API response.
     *
     * @param query The search query string to be processed by the model.
     * @return An [HttpResponse] containing the API response.
     */
    override suspend fun callModel(query: String): HttpResponse {
        val (client, apiKey, baseUrl) = client

        val requestBody =
            ChatCompletionRequest(
                model = "deepseek-chat",
                messages =
                    listOf(
                        Message(role = "system", content = "You are a helpful assistant"),
                        Message(role = "user", content = query),
                    ),
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
