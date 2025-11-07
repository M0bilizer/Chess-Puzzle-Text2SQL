package com.chesspuzzletext2sql

import com.chesspuzzletext2sql.plugins.configureConfiguration
import com.chesspuzzletext2sql.plugins.configureDatabases
import com.chesspuzzletext2sql.plugins.configureEnvironment
import com.chesspuzzletext2sql.plugins.configureHTTP
import com.chesspuzzletext2sql.plugins.configureKoin
import com.chesspuzzletext2sql.plugins.configureMonitoring
import com.chesspuzzletext2sql.plugins.configureRouting
import com.chesspuzzletext2sql.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer

fun main() {
    embeddedServer(
            CIO,
            port = 8080,
            watchPaths = listOf("classes"),
            host = "0.0.0.0",
            module = Application::module,
        )
        .start(wait = true)
}

fun Application.module() {
    configureEnvironment()
    configureConfiguration()
    configureKoin()
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureHTTP()
    configureRouting()
}
