package com.chesspuzzletext2sql

import com.chesspuzzletext2sql.plugins.configureConfigLoader
import com.chesspuzzletext2sql.plugins.configureDatabases
import com.chesspuzzletext2sql.plugins.configureEnvironment
import com.chesspuzzletext2sql.plugins.configureHTTP
import com.chesspuzzletext2sql.plugins.configureKoin
import com.chesspuzzletext2sql.plugins.configureMonitoring
import com.chesspuzzletext2sql.plugins.configureRouting
import com.chesspuzzletext2sql.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*

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
    configureConfigLoader()
    configureKoin()
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureHTTP()
    configureRouting()
}
