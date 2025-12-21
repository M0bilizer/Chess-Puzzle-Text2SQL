package com.chesspuzzletext2sql.features.puzzleSearch.core

import com.chesspuzzletext2sql.errors.ApplicationError
import com.chesspuzzletext2sql.errors.LlmRequestTimeout
import com.chesspuzzletext2sql.errors.LlmServiceUnavailable
import com.chesspuzzletext2sql.errors.LlmTooManyRequests
import com.chesspuzzletext2sql.errors.UnknownError
import com.chesspuzzletext2sql.features.puzzleSearch.models.ChatCompletionRequest
import com.chesspuzzletext2sql.features.puzzleSearch.models.ChatCompletionResponse
import com.chesspuzzletext2sql.features.puzzleSearch.models.LLMConfig
import com.chesspuzzletext2sql.features.puzzleSearch.models.Message
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess

suspend fun HttpClient.callModel(
    model: LLMConfig,
    message: String,
): Result<String, ApplicationError> =
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
            when (response.status) {
                HttpStatusCode.TooManyRequests -> Err(LlmTooManyRequests)
                HttpStatusCode.ServiceUnavailable -> Err(LlmServiceUnavailable)
                else -> Err(UnknownError("Error calling model: ${response.status.description}"))
            }
        }

        val chatCompletion = response.body<ChatCompletionResponse>()
        Ok(chatCompletion.choices.first().message.content)
    } catch (e: Exception) {
        when (e) {
            is HttpRequestTimeoutException -> Err(LlmRequestTimeout)
            else -> Err(UnknownError(e))
        }
    }
