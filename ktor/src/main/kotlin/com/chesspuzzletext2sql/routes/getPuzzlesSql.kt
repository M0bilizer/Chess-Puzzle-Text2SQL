package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.errors.Error
import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.ValidationErrorMessage
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.helpers.handleSystemError
import com.chesspuzzletext2sql.helpers.isAllowed
import com.chesspuzzletext2sql.helpers.isConnected
import com.chesspuzzletext2sql.helpers.isValidSql
import com.chesspuzzletext2sql.helpers.preprocess
import com.chesspuzzletext2sql.helpers.validate
import com.chesspuzzletext2sql.helpers.validateMissing
import com.chesspuzzletext2sql.services.DatabaseService
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
import org.koin.ktor.ext.inject

private val logger = KotlinLogging.logger {}

fun Route.getPuzzlesSql(path: String) {
  val databaseService: DatabaseService by inject()
  get(path) {
    val result = binding {
      val query = validateCall(call).bind()
      isConnected().bind()
      val sql = preprocess(query)
      val puzzles = databaseService.fetchPuzzles(sql).bind()
      puzzles
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
      success = { puzzles -> call.respond(puzzles) },
    )
  }
}

/* ================================================================================================================ */

private fun validateCall(call: RoutingCall): Result<String, Fail> {
  val request = call.request

  return validateMissing(request) { mustNotBeNull("query") }
    .andThen {
      validate(request) {
        must("query") { isValidSql(it) } withMessage ValidationErrorMessage.InvalidQuery
        must("query") { isAllowed(it) } withMessage ValidationErrorMessage.UnallowedQuery
      }
    }
    .map { request.queryParameters["query"]!! }
}
