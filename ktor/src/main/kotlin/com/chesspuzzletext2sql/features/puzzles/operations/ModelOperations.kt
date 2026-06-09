package com.chesspuzzletext2sql.features.puzzles.operations

import com.chesspuzzletext2sql.features.puzzles.domains.ChatCompletionRequest
import com.chesspuzzletext2sql.features.puzzles.domains.ChatCompletionResponse
import com.chesspuzzletext2sql.features.puzzles.domains.LLMConfig
import com.chesspuzzletext2sql.features.puzzles.domains.Message
import com.chesspuzzletext2sql.features.puzzles.domains.SupportedModel
import com.chesspuzzletext2sql.shared.data.repositories.ModelRepository
import com.chesspuzzletext2sql.shared.errors.ApplicationError
import com.chesspuzzletext2sql.shared.errors.LlmRequestTimeout
import com.chesspuzzletext2sql.shared.errors.LlmServiceUnavailable
import com.chesspuzzletext2sql.shared.errors.LlmTooManyRequests
import com.chesspuzzletext2sql.shared.errors.NoModelConfigFound
import com.chesspuzzletext2sql.shared.errors.UnknownError
import com.chesspuzzletext2sql.shared.errors.UnsupportedModel
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

fun getModelConfig(
    model: String?,
    repository: ModelRepository,
): Result<LLMConfig, ApplicationError> =
    when (model) {
        null -> Ok(repository.getDefault())
        is String -> {
            val supportedModel =
                SupportedModel.fromProviderName(model) ?: return Err(UnsupportedModel(model))
            val result =
                repository.getConfig(supportedModel) ?: return Err(NoModelConfigFound(model))
            Ok(result)
        }
    }
