package com.chesspuzzletext2sql.app

import com.chesspuzzletext2sql.config.ApplicationConfigLoader
import com.chesspuzzletext2sql.plugins.configureCors
import com.chesspuzzletext2sql.plugins.configureKoin
import com.chesspuzzletext2sql.plugins.configureMonitoring
import com.chesspuzzletext2sql.plugins.configureRouting
import com.chesspuzzletext2sql.plugins.configureSerialization
import com.github.michaelbull.result.getOrElse
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import kotlin.system.exitProcess

fun main() {
    val dotenv = dotenv()
    val appConfig =
        ApplicationConfigLoader.load(dotenv).getOrElse { errors ->
            System.err.println("Failed to load application configuration:")
            errors.forEach { error -> System.err.println("- $error") }
            exitProcess(1)
        }
    embeddedServer(
        CIO,
        port = 8080,
        watchPaths = listOf("classes"),
        host = "0.0.0.0",
        module = {
            configureCors()
            configureSerialization()
            configureKoin(appConfig)
            configureRouting()
            configureMonitoring()
        },
    )
        .start(wait = true)
}
