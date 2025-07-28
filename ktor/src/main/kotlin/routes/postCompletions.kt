package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.errors.ClientError
import com.chesspuzzletext2sql.errors.CustomError
import com.chesspuzzletext2sql.errors.SystemError
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.helpers.handleSystemError
import com.chesspuzzletext2sql.model.AvailableModels
import com.chesspuzzletext2sql.model.CompletionRequest
import com.chesspuzzletext2sql.model.LLMConfig
import com.chesspuzzletext2sql.model.SupportedModel
import com.chesspuzzletext2sql.model.messages
import com.chesspuzzletext2sql.services.HTTPService
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.fold
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.post
import kotlin.getValue
import org.koin.ktor.ext.inject

fun Route.postCompletions(path: String) {
    val httpService: HTTPService by inject()
    post(path) {
        val result = coroutineBinding {
            val (query, llmConfig) = validateCall(call).bind()
            val chatCompletion =
                httpService
                    .callModel(llmConfig) {
                        messages = messages {
                            system("You are an AI trained to answer questions")
                            user(query)
                        }
                        stream = false
                    }
                    .bind()
            clean(chatCompletion)
        }

        result.fold(
            failure = { err ->
                when (err) {
                    is SystemError -> call.handleSystemError(err)
                    is ClientError -> call.handleClientError(err)
                }
            },
            success = { chatCompletion -> call.respond(chatCompletion) },
        )
    }
}

private suspend fun validateCall(call: RoutingCall): Result<Pair<String, LLMConfig>, CustomError> {
    val request = call.receive<CompletionRequest>()
    if (request.message.isEmpty()) {
        return Err(ClientError.EmptyMessage)
    }
    val model =
        SupportedModel.fromProviderName(request.model) ?: return Err(ClientError.UnsupportedModel)
    val config = AvailableModels[model] ?: return Err(SystemError.UnavailableModel)
    return Ok(request.message to config)
}

private fun clean(string: String): String {
    return string
        .substringAfter("```")
        .substringBefore("```")
        .replace("\n", "")
        .substringAfter("sql")
        .replace(":", "")
        .substringBefore(";")
        .substringBefore("\r")
        .replace("\"", "")
}
