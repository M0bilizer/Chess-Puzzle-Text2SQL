package com.chesspuzzletext2sql.plugins

import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing { get("/hello") { call.respondText("Hello World from Ktor") } }
}
