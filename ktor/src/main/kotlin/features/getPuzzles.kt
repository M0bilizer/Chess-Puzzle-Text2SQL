package com.chesspuzzletext2sql.features

import com.chesspuzzletext2sql.common.getPuzzlesTransaction
import com.chesspuzzletext2sql.common.handleClientError
import com.chesspuzzletext2sql.common.handleSystemError
import com.chesspuzzletext2sql.common.isConnected
import com.chesspuzzletext2sql.errors.CannotConnect
import com.chesspuzzletext2sql.errors.ClientError
import com.chesspuzzletext2sql.errors.CustomError
import com.chesspuzzletext2sql.errors.InvalidLimit
import com.chesspuzzletext2sql.errors.SystemError
import com.chesspuzzletext2sql.errors.UnknownError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get

fun Route.getPuzzles(path: String) {
    get(path) {
        val result = binding {
            val limit = validateCall(call).bind()
            isConnected().bind()
            val puzzles = getPuzzlesTransaction(limit)
            puzzles
        }
        result.fold(
            failure = { err ->
                when (err) {
                    is SystemError -> call.handleSystemError(err)
                    is ClientError -> call.handleClientError(err)
                }
            },
            success = { puzzles -> call.respond(puzzles) },
        )
    }
}

private fun validateCall(call: RoutingCall): Result<Int, CustomError> {
    val limit = call.request.queryParameters["limit"]?.toIntOrNull()
    return when {
        limit == null -> Err(SystemError.CannotConnect)
        limit <= 0 -> Err(ClientError.InvalidLimit)
        limit > 0 -> Ok(limit)
        else -> Err(SystemError.UnknownError)
    }
}
