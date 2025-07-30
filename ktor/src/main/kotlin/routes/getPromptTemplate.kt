package com.chesspuzzletext2sql.routes

import com.chesspuzzletext2sql.config.ApplicationConfig
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.ktor.ext.get

fun Route.getPromptTemplate(path: String) {
    val applicationConfig: ApplicationConfig = get<ApplicationConfig>()
    get(path) {
        val promptTemplate = applicationConfig.promptTemplate
        call.respond(promptTemplate)
    }
}
