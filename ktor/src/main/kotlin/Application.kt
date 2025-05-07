package com.chesspuzzletext2sql

import com.chesspuzzletext2sql.plugins.configureDatabases
import com.chesspuzzletext2sql.plugins.configureHTTP
import com.chesspuzzletext2sql.plugins.configureKoin
import com.chesspuzzletext2sql.plugins.configureMonitoring
import com.chesspuzzletext2sql.plugins.configureRouting
import com.chesspuzzletext2sql.plugins.configureSerialization
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*

fun main() {
    loadEnvIfExists()
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
    configureKoin()
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureHTTP()
    configureRouting()
}

private fun loadEnvIfExists() {
    try {
        val dotenv = dotenv()
        dotenv.entries().forEach { entry -> System.setProperty(entry.key, entry.value) }
        println("Loaded .env file")
    } catch (_: Exception) {
        println(".env file does not exist. Skipping loading environment variables from .env file.")
    }
}
