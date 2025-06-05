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
    this.respond(HttpStatusCode.BadRequest, mapOf("error" to "something", "message" to err.message))
}