package com.chess.puzzle.text2sql.web.service.llm

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.exception.OpenAIHttpException
import com.aallam.openai.api.exception.OpenAIIOException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Component

data class CustomMistralClient(val client: HttpClient, val apiKey: String, val baseUrl: String)

@Serializable private data class Message(val role: String, val content: String)

@Serializable private data class RequestBody(val model: String, val messages: List<Message>)

@Component
class Mistral(private val mistralClient: CustomMistralClient) : LargeLanguageModel {
    override suspend fun callModel(query: String): ChatCompletion {
        val (client, apiKey, baseUrl) = mistralClient

        val requestBody =
            RequestBody(
                model = "mistral-small-latest",
                messages = listOf(Message(role = "user", content = query)),
            )

        val response: HttpResponse =
            client.post(baseUrl) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $apiKey")
                setBody(requestBody)
            }

        return response.body<ChatCompletion>()
    }
}
