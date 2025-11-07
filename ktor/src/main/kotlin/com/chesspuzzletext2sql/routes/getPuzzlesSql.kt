package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.errors.Error
import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.InvalidParameterMessage.CustomConstraint
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.helpers.handleSystemError
import com.chesspuzzletext2sql.helpers.isAllowed
import com.chesspuzzletext2sql.helpers.isConnected
import com.chesspuzzletext2sql.helpers.isValidSql
import com.chesspuzzletext2sql.helpers.preprocess
import com.chesspuzzletext2sql.routes.validation.accessors.query
import com.chesspuzzletext2sql.services.PuzzleService
import com.chesspuzzletext2sql.validators.QueryParsers
import com.chesspuzzletext2sql.validators.QueryValidationConfig
import com.chesspuzzletext2sql.validators.validateQuery
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import dev.nesk.akkurate.Validator
import dev.nesk.akkurate.annotations.Validate
import dev.nesk.akkurate.constraints.constrain
import dev.nesk.akkurate.constraints.otherwise
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

private val logger = KotlinLogging.logger {}

@Serializable @Validate data class PuzzlesSqlRequest(val query: String)

@Serializable data class PuzzlesSqlDto(val query: String)

val puzzlesSqlConfig =
    QueryValidationConfig(
        validator =
            Validator<PuzzlesSqlRequest> {
                query.constrain { isValidSql(it) } otherwise { CustomConstraint.InvalidSql.code }
                query.constrain { isAllowed(it) } otherwise { CustomConstraint.UnallowedSql.code }
            },
        transform = { request: PuzzlesSqlRequest -> PuzzlesSqlDto(request.query) },
        parser = { params -> PuzzlesSqlRequest(query = QueryParsers.stringParser("query")(params)) },
    )

fun Route.getPuzzlesSql(path: String) {
    val puzzleService: PuzzleService by inject()
    get(path) {
        val result = binding {
            val (query) = validateQuery(puzzlesSqlConfig).bind()
            isConnected().bind()
            val sql = preprocess(query)
            val puzzles = puzzleService.selectPuzzles(sql).bind()
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
