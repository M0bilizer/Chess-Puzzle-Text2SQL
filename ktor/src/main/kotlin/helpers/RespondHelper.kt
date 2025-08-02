package com.chesspuzzletext2sql.helpers

import com.chesspuzzletext2sql.errors.ClientError
import com.chesspuzzletext2sql.errors.SystemError
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall

suspend fun RoutingCall.handleSystemError(err: SystemError) {
    this.respond(err.status)
}

suspend fun RoutingCall.handleClientError(err: ClientError) {
    when (err) {
        is ClientError.MultipleErrors -> {
            this.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "validation_errors", "message" to err.errors.map { it.message }),
            )
        }
        else -> {
            this.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to err::class.simpleName, "message" to err.message.toList()),
            )
        }
    }
}
