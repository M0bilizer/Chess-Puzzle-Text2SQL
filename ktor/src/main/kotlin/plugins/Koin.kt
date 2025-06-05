package com.chesspuzzletext2sql.plugins

import com.chesspuzzletext2sql.config.EnvironmentConfig
import com.chesspuzzletext2sql.services.DatabaseService
import com.chesspuzzletext2sql.services.PreprocessingService
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
                single { get<EnvironmentConfig>().file }
                single { DatabaseService() }
                single {
                    PreprocessingService(
                        promptTemplatePath = get<EnvironmentConfig.FileConfig>().promptTemplatePath
                    )
                }
            }
        )
    }
}
