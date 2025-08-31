package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.errors.Error
import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.ValidationErrorMessage
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.helpers.handleSystemError
import com.chesspuzzletext2sql.helpers.preprocess
import com.chesspuzzletext2sql.helpers.validateJson
import com.chesspuzzletext2sql.model.AvailableModels
import com.chesspuzzletext2sql.model.AvailablePromptTemplate
import com.chesspuzzletext2sql.model.LLMConfig
import com.chesspuzzletext2sql.model.PromptTemplate
import com.chesspuzzletext2sql.model.SupportedModel
import com.chesspuzzletext2sql.services.LLMClient
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable

private val logger = KotlinLogging.logger {}

fun Route.postChatText2Sql(path: String) {
  post(path) {
    val result = coroutineBinding {
      val (text, promptTemplate, llmConfig) = validateCall(call).bind()
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

/* ================================================================================================================ */

@Serializable
private data class ChatText2SqlRequest(val text: String, val template: String, val model: String)

private data class ChatText2SqlDto(
  val text: String,
  val promptTemplate: PromptTemplate,
  val llmConfig: LLMConfig,
) {
  companion object {
    fun from(request: ChatText2SqlRequest, promptTemplate: PromptTemplate, config: LLMConfig) =
      ChatText2SqlDto(request.text, promptTemplate, config)
  }
}

private suspend fun validateCall(call: RoutingCall): Result<ChatText2SqlDto, Fail> {
  val request = call.receive<ChatText2SqlRequest>()

  return validateJson(request) {
      must("text") { it is String && it.isNotEmpty() } withMessage
        ValidationErrorMessage.EmptyMessage
      must("template") { it is String && it.isNotEmpty() } withMessage
        ValidationErrorMessage.EmptyTemplate
      must("template") { it is String && AvailablePromptTemplate[it] != null } withMessage
        ValidationErrorMessage.UnsupportedTemplate
      must("model") { it is String && SupportedModel.fromProviderName(it) != null } withMessage
        ValidationErrorMessage.UnsupportedModel
      must("model") {
        it is String &&
          SupportedModel.fromProviderName(it)?.let { model -> AvailableModels[model] != null }
            ?: false
      } withMessage ValidationErrorMessage.UnavailableModel
    }
    .map {
      val promptTemplate = AvailablePromptTemplate[request.template]!!
      val config = AvailableModels[SupportedModel.fromProviderName(request.model)!!]!!
      ChatText2SqlDto.from(request, promptTemplate, config)
    }
}
