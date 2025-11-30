package com.chesspuzzletext2sql.config

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrThrow
import com.github.michaelbull.result.runCatching
import io.github.cdimascio.dotenv.Dotenv

data class ApplicationConfig(val environment: EnvironmentConfig, val files: FilesConfig)

object ApplicationConfigLoader {
    fun load(dotenv: Dotenv): Result<ApplicationConfig, Throwable> = runCatching {
        ApplicationConfig(
            environment = EnvironmentConfigLoader.load(dotenv).getOrThrow(),
            files = FileConfigLoader.loadTemplates().getOrThrow(),
        )
    }
}
