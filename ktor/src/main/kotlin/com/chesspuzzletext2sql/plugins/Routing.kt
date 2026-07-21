package com.chesspuzzletext2sql.plugins

import com.chesspuzzletext2sql.features.puzzles.puzzles
import com.chesspuzzletext2sql.shared.data.repositories.ModelRepository
import com.chesspuzzletext2sql.shared.data.repositories.PuzzleRepository
import com.chesspuzzletext2sql.shared.data.repositories.TemplateRepository
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val templateRepository by inject<TemplateRepository>()
    val modelRepository by inject<ModelRepository>()
    val puzzleRepository by inject<PuzzleRepository>()
    val client by inject<HttpClient>()

    routing {
        get("/health") { call.respond(HttpStatusCode.OK) }

        puzzles(templateRepository, modelRepository, puzzleRepository, client)
    }
}
