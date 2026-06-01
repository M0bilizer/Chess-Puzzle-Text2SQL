package com.chesspuzzletext2sql.plugins

import com.chesspuzzletext2sql.config.ApplicationConfig
import com.chesspuzzletext2sql.createAppModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin(appConfig: ApplicationConfig) {
    install(Koin) {
        slf4jLogger()
        modules(createAppModule(appConfig))
    }
}
