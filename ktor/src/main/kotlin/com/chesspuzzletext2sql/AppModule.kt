package com.chesspuzzletext2sql

import com.chesspuzzletext2sql.features.puzzles.data.ModelRepositoryImp
import com.chesspuzzletext2sql.features.puzzles.data.PuzzleRepositoryImp
import com.chesspuzzletext2sql.features.puzzles.data.TemplateRepositoryImp
import com.chesspuzzletext2sql.shared.config.ApplicationConfig
import com.chesspuzzletext2sql.shared.data.DatabaseFactory.createDatabase
import com.chesspuzzletext2sql.shared.data.repositories.ModelRepository
import com.chesspuzzletext2sql.shared.data.repositories.PuzzleRepository
import com.chesspuzzletext2sql.shared.data.repositories.TemplateRepository
import com.chesspuzzletext2sql.shared.http.HttpClientFactory.createHttpClient
import org.koin.dsl.module

fun createAppModule(appConfig: ApplicationConfig) = module {
    single { appConfig }

    single<TemplateRepository> {
        TemplateRepositoryImp(appConfig.files.templates, appConfig.files.default)
    }

    single<ModelRepository> {
        ModelRepositoryImp(appConfig.environment.llmConfigs, appConfig.environment.defaultConfig)
    }

    single<PuzzleRepository> { PuzzleRepositoryImp(get()) }

    single { createDatabase(appConfig.environment.database) }

    single { createHttpClient() }
}
