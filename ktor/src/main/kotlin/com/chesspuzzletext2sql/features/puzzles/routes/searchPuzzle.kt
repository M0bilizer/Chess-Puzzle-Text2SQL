package com.chesspuzzletext2sql.features.puzzles.routes

import com.chesspuzzletext2sql.features.llm.core.callModel
import com.chesspuzzletext2sql.features.llm.core.getModelConfig
import com.chesspuzzletext2sql.features.llm.data.ModelRepository
import com.chesspuzzletext2sql.features.prompts.core.getPromptTemplate
import com.chesspuzzletext2sql.features.prompts.data.TemplateRepository
import com.chesspuzzletext2sql.features.puzzles.core.preprocess
import com.chesspuzzletext2sql.features.puzzles.data.PuzzleRepository
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.fold
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
    get("/api/puzzles") {
        val search = call.request.queryParameters["search"]
        if (search == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing query")
            return@get
        }
        val template = call.request.queryParameters["template"]
        val model = call.request.queryParameters["model"]

        val result = coroutineBinding {
            val promptTemplate = getPromptTemplate(template, templateRepository).bind()
            val modelConfig = getModelConfig(model, modelRepository).bind()
            val sql = client.callModel(modelConfig, promptTemplate(search)).bind()
            val cleaned = preprocess(sql)
            puzzleRepository.selectPuzzles(cleaned).bind()
        }
        result.fold(
            { puzzles -> call.respond(puzzles) },
            { throwable -> call.respond(throwable.message ?: "oops") },
        )
    }
}
