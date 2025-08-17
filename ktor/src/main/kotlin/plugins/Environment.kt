package com.chesspuzzletext2sql.plugins

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.Application

fun Application.configureEnvironment() {
  try {
    val dotenv = dotenv { systemProperties = true }
    println("Loaded .env file")
  } catch (_: Exception) {
    println(".env file does not exist. Skipping loading environment variables from .env file.")
  }
}
