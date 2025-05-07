package com.chesspuzzletext2sql.plugins

import com.chesspuzzletext2sql.config.EnvironmentConfig
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single { EnvironmentConfig() }
                single { get<EnvironmentConfig>().database }
            }
        )
    }
}
