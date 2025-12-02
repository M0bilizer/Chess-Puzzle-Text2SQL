package com.chesspuzzletext2sql.plugins

import com.chesspuzzletext2sql.features.llm.data.ModelRepository
import com.chesspuzzletext2sql.features.prompts.data.TemplateRepository
import com.chesspuzzletext2sql.features.puzzles.data.PuzzleRepository
import com.chesspuzzletext2sql.features.puzzles.routes.searchPuzzle
import io.ktor.client.HttpClient
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val templateRepository by inject<TemplateRepository>()
    val modelRepository by inject<ModelRepository>()
    val puzzleRepository by inject<PuzzleRepository>()
    val client by inject<HttpClient>()

    routing {
        get("/hello") { call.respondText("Hello World from Ktor") }

        searchPuzzle(templateRepository, modelRepository, puzzleRepository, client)
    }
}
