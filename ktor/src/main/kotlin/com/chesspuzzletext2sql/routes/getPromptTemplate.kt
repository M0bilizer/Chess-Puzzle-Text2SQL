package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.ValidationErrorMessage
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.helpers.validate
import com.chesspuzzletext2sql.helpers.validateMissing
import com.chesspuzzletext2sql.model.AvailablePromptTemplate
import com.chesspuzzletext2sql.model.PromptTemplate
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
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

private fun validateCall(call: RoutingCall): Result<PromptTemplate, Fail> {
  val request = call.request

  return validateMissing(request) { mustNotBeNull("template") }
    .andThen {
      validate(request) {
        must("template") { AvailablePromptTemplate[it] != null } withMessage
          ValidationErrorMessage.UnsupportedTemplate
      }
    }
    .map { AvailablePromptTemplate[request.queryParameters["template"]!!]!! }
}
