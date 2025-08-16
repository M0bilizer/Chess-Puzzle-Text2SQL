package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.errors.ClientError
import com.chesspuzzletext2sql.errors.SystemError
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.helpers.handleSystemError
import com.chesspuzzletext2sql.model.AvailableModels
import com.chesspuzzletext2sql.model.LLMConfig
import com.chesspuzzletext2sql.model.Message
import com.chesspuzzletext2sql.model.SupportedModel
import com.chesspuzzletext2sql.services.LLMClient
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.fold
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.post
import kotlin.getValue
import kotlinx.serialization.Serializable

private val logger = KotlinLogging.logger {}

fun Route.postChatCompletions(path: String) {
    post(path) {
        val result = coroutineBinding {
            val (query, llmConfig) = validateCall(call).bind()
            val llmClient = LLMClient(llmConfig)
            val chatCompletion =
                llmClient
                    .call(
                        messages =
                            listOf(
                                Message("system", "You are an AI trained to answer questions"),
                                Message("user", query),
                            )
                    )
                    .bind()
            chatCompletion
        }

        result.fold(
            failure = { err ->
                when (err) {
                    is SystemError -> {
                        logger.error { err.message }
                        call.handleSystemError(err)
                    }
                    is ClientError -> call.handleClientError(err)
                }
            },
            success = { chatCompletion -> call.respond(chatCompletion) },
        )
    }
}

/* ================================================================================================================ */

@Serializable private data class ChatCompletionRequest(val message: String, val model: String)

private data class ChatCompletionDto(val query: String, val llmConfig: LLMConfig) {
    companion object {
        fun from(request: ChatCompletionRequest, config: LLMConfig) =
            ChatCompletionDto(request.message, config)
    }
}

private suspend fun validateCall(call: RoutingCall): Result<ChatCompletionDto, ClientError> {
    val request = call.receive<ChatCompletionRequest>()
    if (request.message.isBlank()) {
        return Err(ClientError.EmptyMessage)
    }
    val model =
        SupportedModel.fromProviderName(request.model) ?: return Err(ClientError.UnsupportedModel)
    val config = AvailableModels[model] ?: return Err(ClientError.UnavailableModel)
    return Ok(ChatCompletionDto.from(request, config))
}
