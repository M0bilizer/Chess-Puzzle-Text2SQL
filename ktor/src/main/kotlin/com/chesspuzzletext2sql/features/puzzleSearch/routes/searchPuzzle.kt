package com.chesspuzzletext2sql.features.puzzleSearch.routes

import com.chesspuzzletext2sql.features.puzzleSearch.core.callModel
import com.chesspuzzletext2sql.features.puzzleSearch.core.getModelConfig
import com.chesspuzzletext2sql.features.puzzleSearch.core.getPromptTemplate
import com.chesspuzzletext2sql.features.puzzleSearch.core.preprocessSqlStatement
import com.chesspuzzletext2sql.features.puzzleSearch.core.selectPuzzles
import com.chesspuzzletext2sql.features.puzzleSearch.data.ModelRepository
import com.chesspuzzletext2sql.features.puzzleSearch.data.PuzzleRepository
import com.chesspuzzletext2sql.features.puzzleSearch.data.TemplateRepository
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.onFailure
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.searchPuzzle(
    templateRepository: TemplateRepository,
    modelRepository: ModelRepository,
    puzzleRepository: PuzzleRepository,
    client: HttpClient,
) {
    val logger = KotlinLogging.logger {}

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
                    .onFailure { err ->
                        logger.error { "Failed to get prompt template '$template'. Result: $err" }
                    }
                    .bind()
            val modelConfig =
                getModelConfig(model, modelRepository)
                    .onFailure { err ->
                        logger.error { "Failed to get model config '$model'. Result: $err" }
                    }
                    .bind()
            val sql =
                client
                    .callModel(modelConfig, promptTemplate(search))
                    .onFailure { err ->
                        logger.error {
                            "Failed to call model '$modelConfig' with '$search'. Result: $err"
                        }
                    }
                    .bind()
            val cleaned = preprocessSqlStatement(sql)
            val puzzles =
                selectPuzzles(cleaned, puzzleRepository)
                    .onFailure { err ->
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
