package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.errors.ClientError
import com.chesspuzzletext2sql.errors.SystemError
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.helpers.handleSystemError
import com.chesspuzzletext2sql.helpers.isConnected
import com.chesspuzzletext2sql.services.DatabaseService
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
import org.koin.ktor.ext.inject

private val logger = KotlinLogging.logger {}

fun Route.getPuzzlesRandom(path: String) {
    val databaseService: DatabaseService by inject()
    get(path) {
        val result = binding {
            val limit = validateCall(call).bind()
            isConnected().bind()
            val puzzles = databaseService.getPuzzlesTransaction(limit)
            puzzles
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
            success = { puzzles -> call.respond(puzzles) },
        )
    }
}

/* ================================================================================================================ */

private fun validateCall(call: RoutingCall): Result<Int, ClientError> {
    val count = call.request.queryParameters["count"]?.toIntOrNull() ?: 1
    return when (count) {
        in Int.MIN_VALUE..0 -> Err(ClientError.InvalidCount)
        else -> Ok(count)
    }
}
