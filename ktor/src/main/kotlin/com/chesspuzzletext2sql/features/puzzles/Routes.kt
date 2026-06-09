package com.chesspuzzletext2sql.features.puzzles

import com.chesspuzzletext2sql.features.puzzles.operations.callModel
import com.chesspuzzletext2sql.features.puzzles.operations.getModelConfig
import com.chesspuzzletext2sql.features.puzzles.operations.getPromptTemplate
import com.chesspuzzletext2sql.features.puzzles.operations.getPuzzleById
import com.chesspuzzletext2sql.features.puzzles.operations.selectPuzzles
import com.chesspuzzletext2sql.shared.data.repositories.ModelRepository
import com.chesspuzzletext2sql.shared.data.repositories.PuzzleRepository
import com.chesspuzzletext2sql.shared.data.repositories.TemplateRepository
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.onErr
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.puzzles(
    templateRepository: TemplateRepository,
    modelRepository: ModelRepository,
    puzzleRepository: PuzzleRepository,
    client: HttpClient,
) {
    val logger = KotlinLogging.logger {}

    get("/api/puzzles/{id}") {
        val puzzleId = call.parameters["id"]
        if (puzzleId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing id")
            return@get
        }

        val result = coroutineBinding {
            val puzzle =
                getPuzzleById(puzzleId, puzzleRepository)
                    .onErr { err -> logger.error { "Failed to get puzzle by Id. Result: $err" } }
                    .bind()
            puzzle
        }
        result.fold(
            { puzzle -> call.respond(puzzle) },
            { error -> call.respond(error.status, error.response) },
        )
    }

    get("/api/puzzles") {
        val search = call.request.queryParameters["search"]
        if (search == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing query")
            return@get
        }
        val template = call.request.queryParameters["template"]
        val model = call.request.queryParameters["model"]

        val result = coroutineBinding {
            val promptTemplate =
                getPromptTemplate(template, templateRepository)
                    .onErr { err ->
                        logger.error { "Failed to get prompt template '$template'. Result: $err" }
                    }
                    .bind()
            val modelConfig =
                getModelConfig(model, modelRepository)
                    .onErr { err ->
                        logger.error { "Failed to get model config '$model'. Result: $err" }
                    }
                    .bind()
            val sql =
                client
                    .callModel(modelConfig, promptTemplate(search))
                    .onErr { err ->
                        logger.error {
                            "Failed to call model '$modelConfig' with '$search'. Result: $err"
                        }
                    }
                    .bind()
            val cleaned = preprocessSqlStatement(sql)
            val puzzles =
                selectPuzzles(cleaned, puzzleRepository)
                    .onErr { err ->
                        logger.error {
                            "Failed to select puzzles from database with '$cleaned'. Result: $err"
                        }
                    }
                    .bind()
            puzzles
        }
        result.fold(
            { puzzles -> call.respond(puzzles) },
            { error -> call.respond(error.status, error.response) },
        )
    }
}
