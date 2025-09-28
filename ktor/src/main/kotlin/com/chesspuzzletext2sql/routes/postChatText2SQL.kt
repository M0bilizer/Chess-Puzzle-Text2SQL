package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.errors.Error
import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.InvalidParameterMessage.CustomConstraint
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.helpers.handleSystemError
import com.chesspuzzletext2sql.helpers.preprocess
import com.chesspuzzletext2sql.model.AvailableModels
import com.chesspuzzletext2sql.model.AvailablePromptTemplate
import com.chesspuzzletext2sql.model.LLMConfig
import com.chesspuzzletext2sql.model.PromptTemplate
import com.chesspuzzletext2sql.model.SupportedModel
import com.chesspuzzletext2sql.routes.validation.accessors.model
import com.chesspuzzletext2sql.routes.validation.accessors.template
import com.chesspuzzletext2sql.routes.validation.accessors.text
import com.chesspuzzletext2sql.services.LLMClient
import com.chesspuzzletext2sql.services.ValidationConfig
import com.chesspuzzletext2sql.services.validateRequest
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

@Serializable
@Validate
data class ChatText2SqlRequest(val text: String, val template: String, val model: String)

data class ChatText2SqlDto(
    val text: String,
    val promptTemplate: PromptTemplate,
    val llmConfig: LLMConfig,
)

val chatText2SqlValidation =
    ValidationConfig(
        validator =
            Validator<ChatText2SqlRequest> {
                text.isNotEmpty()
                val (isValidTemplate) = template.isNotEmpty()
                if (isValidTemplate) {
                    template.constrain { AvailablePromptTemplate[it] != null } otherwise
                        {
                            CustomConstraint.UnsupportedTemplate.code
                        }
                }
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
        transform = { request: ChatText2SqlRequest ->
            val promptTemplate = AvailablePromptTemplate[request.template]!!
            val config = AvailableModels[SupportedModel.fromProviderName(request.model)!!]!!
            ChatText2SqlDto(request.text, promptTemplate, config)
        },
    )

fun Route.postChatText2Sql(path: String) {
    post(path) {
        val result = coroutineBinding {
            val (text, promptTemplate, llmConfig) = validateRequest(chatText2SqlValidation).bind()
            val chatCompletion =
                LLMClient(llmConfig).call(template = promptTemplate, userInput = text).bind()
            preprocess(chatCompletion)
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
            success = { sql -> call.respond(sql) },
        )
    }
}
