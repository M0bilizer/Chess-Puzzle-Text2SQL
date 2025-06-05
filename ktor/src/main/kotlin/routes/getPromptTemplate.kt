package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.config.ApplicationConfig
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.getPromptTemplate(path: String) {
    get(path) {
        val promptTemplate = ApplicationConfig.PROMPT_TEMPLATE
        call.respond(promptTemplate)
    }
}
