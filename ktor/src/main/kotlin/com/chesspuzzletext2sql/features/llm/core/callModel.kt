package com.chesspuzzletext2sql.features.llm.core

import com.chesspuzzletext2sql.features.llm.models.ChatCompletionRequest
import com.chesspuzzletext2sql.features.llm.models.ChatCompletionResponse
import com.chesspuzzletext2sql.features.llm.models.LLMConfig
import com.chesspuzzletext2sql.features.llm.models.Message
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

suspend fun HttpClient.callModel(model: LLMConfig, message: String): Result<String, Throwable> =
    try {
        val request =
            ChatCompletionRequest(
                model = model.modelName,
                messages = listOf(Message("user", message)),
                stream = false,
                temperature = null,
                maxTokens = null,
            )

        val response =
            this.post(model.baseUrl) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${model.apiKey}")
                setBody(request)
            }

        if (!response.status.isSuccess()) {
            throw Throwable("Not success")
        }

        val chatCompletion = response.body<ChatCompletionResponse>()
        Ok(chatCompletion.choices.first().message.content)
    } catch (e: Exception) {
        Err(Throwable(e.message))
    }
