package com.chesspuzzletext2sql.helpers

import com.chesspuzzletext2sql.errors.ClientError
import com.chesspuzzletext2sql.errors.SystemError
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingCall
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

suspend fun RoutingCall.handleSystemError(err: SystemError) {
  respond(err.status)
}

@Serializable private class MultipleErrorResponse(val error: String, val message: List<String>)

suspend fun RoutingCall.handleClientError(err: ClientError) {
  when (err) {
    is ClientError.MultipleErrors -> {
      require(err.size > 0)
      val message =
        Json.encodeToString(MultipleErrorResponse("multiple_errors", err.errors.map { it.message }))
      respondText(
        status = HttpStatusCode.BadRequest,
        text = message,
        contentType = ContentType.Application.Json,
      )
    }
    else ->
      respond(
        status = HttpStatusCode.BadRequest,
        message =
          mapOf("error" to (err::class.simpleName ?: "client_error"), "message" to err.message),
      )
  }
}
