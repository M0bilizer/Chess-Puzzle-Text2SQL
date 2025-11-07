package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.errors.Error
import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.helpers.handleClientError
import com.chesspuzzletext2sql.helpers.handleSystemError
import com.chesspuzzletext2sql.helpers.isConnected
import com.chesspuzzletext2sql.routes.validation.accessors.count
import com.chesspuzzletext2sql.services.PuzzleService
import com.chesspuzzletext2sql.validators.QueryParsers
import com.chesspuzzletext2sql.validators.QueryValidationConfig
import com.chesspuzzletext2sql.validators.validateQuery
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import dev.nesk.akkurate.Validator
import dev.nesk.akkurate.annotations.Validate
import dev.nesk.akkurate.constraints.builders.isPositive
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

private val logger = KotlinLogging.logger {}

@Serializable @Validate data class PuzzlesRandomRequest(val count: Int)

@Serializable data class PuzzlesRandomDto(val limit: Int)

val puzzlesRandomConfig =
    QueryValidationConfig(
        validator = Validator<PuzzlesRandomRequest> { count.isPositive() },
        transform = { request: PuzzlesRandomRequest -> PuzzlesRandomDto(request.count) },
        parser = { params ->
            PuzzlesRandomRequest(count = QueryParsers.intParser("count", 0)(params))
        },
    )

fun Route.getPuzzlesRandom(path: String) {
    val puzzleService: PuzzleService by inject()
    get(path) {
        val result = binding {
            val (limit) = validateQuery(puzzlesRandomConfig).bind()
            isConnected().bind()
            val puzzles = puzzleService.getPuzzles(limit)
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
