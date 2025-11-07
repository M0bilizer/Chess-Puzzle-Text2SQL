package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.clients.LLMClient
import com.chesspuzzletext2sql.errors.Error
import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.InvalidParameterMessage.CustomConstraint
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.helpers.handleSystemError
import com.chesspuzzletext2sql.model.AvailableModels
import com.chesspuzzletext2sql.model.LLMConfig
import com.chesspuzzletext2sql.model.Message
import com.chesspuzzletext2sql.model.SupportedModel
import com.chesspuzzletext2sql.routes.validation.accessors.message
import com.chesspuzzletext2sql.routes.validation.accessors.model
import com.chesspuzzletext2sql.validators.ValidationConfig
import com.chesspuzzletext2sql.validators.validateRequest
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.fold
import dev.nesk.akkurate.Validator
import dev.nesk.akkurate.annotations.Validate
import dev.nesk.akkurate.constraints.builders.isNotEmpty
import dev.nesk.akkurate.constraints.constrain
import dev.nesk.akkurate.constraints.otherwise
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable

private val logger = KotlinLogging.logger {}

@Serializable @Validate data class ChatCompletionsRequest(val message: String, val model: String)

data class ChatCompletionsDto(val query: String, val llmConfig: LLMConfig)

val chatCompletionsValidation =
    ValidationConfig(
        validator =
            Validator<ChatCompletionsRequest> {
                message.isNotEmpty()
                val (isValidModel) = model.isNotEmpty()
                if (isValidModel) {
                    val (isSupported) =
                        model.constrain { SupportedModel.fromProviderName(it) != null } otherwise
                            {
                                CustomConstraint.UnsupportedModel.code
                            }
                    if (isSupported) {
                        model.constrain {
                            AvailableModels[SupportedModel.fromProviderName(it)!!] != null
                        } otherwise { CustomConstraint.UnavailableModel.code }
                    }
                }
            },
        transform = { request: ChatCompletionsRequest ->
            val model = SupportedModel.fromProviderName(request.model)!!
            val config = AvailableModels[model]!!
            ChatCompletionsDto(request.message, config)
        },
    )

fun Route.postChatCompletions(path: String) {
    post(path) {
        val result = coroutineBinding {
            val (query, llmConfig) = validateRequest(chatCompletionsValidation).bind()
            val chatCompletion =
                LLMClient(llmConfig)
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
                    is Error -> {
                        logger.error { err.type }
                        call.handleSystemError(err)
                    }

                    is Fail -> call.handleClientError(err)
                }
            },
            success = { chatCompletion -> call.respond(chatCompletion) },
        )
    }
}
