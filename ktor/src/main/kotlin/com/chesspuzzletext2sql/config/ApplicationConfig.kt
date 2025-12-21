package com.chesspuzzletext2sql.config

import com.chesspuzzletext2sql.errors.StartupError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.cdimascio.dotenv.Dotenv

data class ApplicationConfig(val environment: EnvironmentConfig, val files: FilesConfig)

object ApplicationConfigLoader {
    fun load(dotenv: Dotenv): Result<ApplicationConfig, List<StartupError>> {
        val environment = EnvironmentConfigLoader.load(dotenv)
        val files = FileConfigLoader.loadTemplates()

        val errors = mutableListOf<StartupError>()

        if (environment.isErr) errors.addAll(environment.error)
        if (files.isErr) errors.addAll(files.error)

        return if (errors.isEmpty()) {
            Ok(ApplicationConfig(environment.value, files.value))
        } else {
            Err(errors)
        }
    }
}
