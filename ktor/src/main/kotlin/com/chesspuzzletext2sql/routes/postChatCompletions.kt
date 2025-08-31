package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.errors.Error
import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.ValidationErrorMessage
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.helpers.handleSystemError
import com.chesspuzzletext2sql.helpers.validateJson
import com.chesspuzzletext2sql.model.AvailableModels
import com.chesspuzzletext2sql.model.LLMConfig
import com.chesspuzzletext2sql.model.Message
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

fun Route.postChatCompletions(path: String) {
  post(path) {
    val result = coroutineBinding {
      val (query, llmConfig) = validateCall(call).bind()
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

/* ================================================================================================================ */

@Serializable private data class ChatCompletionRequest(val message: String, val model: String)

private data class ChatCompletionDto(val query: String, val llmConfig: LLMConfig) {
  companion object {
    fun from(request: ChatCompletionRequest, config: LLMConfig) =
      ChatCompletionDto(request.message, config)
  }
}

private suspend fun validateCall(call: RoutingCall): Result<ChatCompletionDto, Fail> {
  val request = call.receive<ChatCompletionRequest>()

  return validateJson(request) {
      must("message") { it is String && it.isNotBlank() } withMessage
        ValidationErrorMessage.EmptyMessage
      must("model") { it is String && SupportedModel.fromProviderName(it) != null } withMessage
        ValidationErrorMessage.UnsupportedModel
      must("model") {
        it is String &&
          SupportedModel.fromProviderName(it)?.let { model -> AvailableModels[model] != null }
            ?: false
      } withMessage ValidationErrorMessage.UnavailableModel
    }
    .map {
      val model = SupportedModel.fromProviderName(request.model)!!
      val config = AvailableModels[model]!!
      ChatCompletionDto.from(request, config)
    }
}
