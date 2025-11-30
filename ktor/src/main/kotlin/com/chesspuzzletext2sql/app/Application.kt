package com.chesspuzzletext2sql.app

import com.chesspuzzletext2sql.app.di.createAppModule
import com.chesspuzzletext2sql.config.ApplicationConfigLoader
import com.github.michaelbull.result.getOrElse
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import kotlin.system.exitProcess

fun main() {
    val dotenv = dotenv()
    val appConfig =
        ApplicationConfigLoader.load(dotenv).getOrElse { error ->
            System.err.println("Failed to load application configuration: ${error.message}")
            error.printStackTrace()
            exitProcess(1)
        }
    embeddedServer(
            CIO,
            port = 8080,
            watchPaths = listOf("classes"),
            host = "0.0.0.0",
            module = { createAppModule(appConfig) },
        )
        .start(wait = true)
}
