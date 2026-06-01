package com.chesspuzzletext2sql

import com.chesspuzzletext2sql.config.ApplicationConfig
import com.chesspuzzletext2sql.features.puzzleSearch.data.ModelRepository
import com.chesspuzzletext2sql.features.puzzleSearch.data.ModelRepositoryImp
import com.chesspuzzletext2sql.features.puzzleSearch.data.PuzzleRepository
import com.chesspuzzletext2sql.features.puzzleSearch.data.PuzzleRepositoryImp
import com.chesspuzzletext2sql.features.puzzleSearch.data.TemplateRepository
import com.chesspuzzletext2sql.features.puzzleSearch.data.TemplateRepositoryImp
import com.chesspuzzletext2sql.shared.data.DatabaseFactory.createDatabase
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
