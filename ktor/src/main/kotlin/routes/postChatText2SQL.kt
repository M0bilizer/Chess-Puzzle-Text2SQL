package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.errors.ClientError
import com.chesspuzzletext2sql.errors.CustomError
import com.chesspuzzletext2sql.errors.SystemError
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.helpers.handleSystemError
import com.chesspuzzletext2sql.helpers.preprocess
import com.chesspuzzletext2sql.model.AvailableModels
import com.chesspuzzletext2sql.model.AvailablePromptTemplate
import com.chesspuzzletext2sql.model.LLMConfig
import com.chesspuzzletext2sql.model.PromptTemplate
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
          is SystemError -> {
            logger.error { err.message }
            call.handleSystemError(err)
          }
          is ClientError -> call.handleClientError(err)
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

private suspend fun validateCall(call: RoutingCall): Result<ChatText2SqlDto, CustomError> {
  val request = call.receive<ChatText2SqlRequest>()
  val multipleErrors =
    ClientError.collect {
      addIf(request.text.isEmpty(), ClientError.EmptyMessage)
      addIf(request.template.isEmpty(), ClientError.EmptyTemplate)

      val promptTemplate = AvailablePromptTemplate[request.template]
      addIf(promptTemplate == null, ClientError.UnsupportedTemplate)

      val model = SupportedModel.fromProviderName(request.model)
      addIf(model == null, ClientError.UnsupportedModel)
      model?.let { addIf(AvailableModels[it] == null, ClientError.UnavailableModel) }
    }

  return if (multipleErrors.size > 0) Err(multipleErrors)
  else {
    val promptTemplate = AvailablePromptTemplate[request.template]!!
    val config = AvailableModels[SupportedModel.fromProviderName(request.model)!!]!!
    Ok(ChatText2SqlDto.from(request, promptTemplate, config))
  }
}
