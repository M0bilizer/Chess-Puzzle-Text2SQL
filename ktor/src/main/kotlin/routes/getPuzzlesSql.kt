package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.errors.ClientError
import com.chesspuzzletext2sql.errors.SystemError
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.helpers.handleSystemError
import com.chesspuzzletext2sql.helpers.isAllowed
import com.chesspuzzletext2sql.helpers.isConnected
import com.chesspuzzletext2sql.helpers.isValidSql
import com.chesspuzzletext2sql.helpers.preprocess
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
                    is SystemError -> call.handleSystemError(err)
                    is ClientError -> call.handleClientError(err)
                }
            },
            success = { puzzles -> call.respond(puzzles) },
        )
    }
}

private fun validateCall(call: RoutingCall): Result<String, ClientError> {
    val query = call.request.queryParameters["query"]
    if (query.isNullOrBlank()) return Err(ClientError.EmptyQuery)
    if (!isValidSql(query)) return Err(ClientError.InvalidQuery)
    if (!isAllowed((query))) return Err(ClientError.UnallowedQuery)
    return Ok(query)
}
