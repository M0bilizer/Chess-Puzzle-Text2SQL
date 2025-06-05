package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.errors.CannotConnect
import com.chesspuzzletext2sql.errors.ClientError
import com.chesspuzzletext2sql.errors.CustomError
import com.chesspuzzletext2sql.errors.InvalidLimit
import com.chesspuzzletext2sql.errors.SystemError
import com.chesspuzzletext2sql.errors.UnknownError
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.helpers.handleSystemError
import com.chesspuzzletext2sql.helpers.isConnected
import com.chesspuzzletext2sql.services.DatabaseService
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject

fun Route.getPuzzles(path: String) {
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
