package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.errors.ClientError
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.model.AvailablePromptTemplate
import com.chesspuzzletext2sql.model.PromptTemplate
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get

private val logger = KotlinLogging.logger {}

fun Route.getPromptTemplate(path: String) {
  get(path) {
    val result = binding {
      val promptTemplate = validateCall(call).bind()
      promptTemplate
    }
    result.fold(
      failure = { err -> call.handleClientError(err) },
      success = { promptTemplate -> call.respond(promptTemplate) },
    )
  }
}

/* ================================================================================================================ */

private fun validateCall(call: RoutingCall): Result<PromptTemplate, ClientError> {
  val template = call.request.queryParameters["template"]
  if (template.isNullOrBlank()) return Err(ClientError.EmptyTemplate)
  val promptTemplate =
    AvailablePromptTemplate[template] ?: return Err(ClientError.UnsupportedTemplate)
  return Ok(promptTemplate)
}
