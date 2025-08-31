package com.chesspuzzletext2sql.helpers

import com.chesspuzzletext2sql.errors.Error
import com.chesspuzzletext2sql.errors.Fail
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import kotlinx.serialization.Serializable

suspend fun RoutingCall.handleSystemError(err: Error) {
  respond(err.status)
}

@Serializable
data class ErrorResponse(
  val code: String,
  val message: String,
  val details: List<Map<String, String>>,
)

suspend fun RoutingCall.handleClientError(err: Fail) {
  println(err.toString())
  val details =
    err.details.map { detail ->
      mapOf(
        "field" to detail.field,
        "code" to detail.code,
        "description" to detail.message.description,
      )
    }

  val response = ErrorResponse(code = err.type.code, message = err.type.message, details = details)
  respond(status = HttpStatusCode.BadRequest, response)
}
