package com.chesspuzzletext2sql.plugins

import com.chesspuzzletext2sql.config.EnvironmentConfig
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.koin.ktor.ext.inject

fun Application.configureDatabases() {
    val config by inject<EnvironmentConfig.DatabaseConfig>()

    Database.connect(
        url = config.url,
        driver = config.driver,
        user = config.user,
        password = config.password,
    )
}
