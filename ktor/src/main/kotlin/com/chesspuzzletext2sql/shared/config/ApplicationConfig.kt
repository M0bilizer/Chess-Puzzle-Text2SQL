package com.chesspuzzletext2sql.shared.config

import com.chesspuzzletext2sql.shared.errors.StartupError
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.zipOrAccumulate
import io.github.cdimascio.dotenv.Dotenv

data class ApplicationConfig(val environment: EnvironmentConfig, val files: FilesConfig)

object ApplicationConfigLoader {
    fun load(dotenv: Dotenv): Result<ApplicationConfig, List<StartupError>> {
        return zipOrAccumulate(
                { EnvironmentConfigLoader.load(dotenv) },
                { FileConfigLoader.loadTemplates() },
            ) { environment, files ->
                ApplicationConfig(environment, files)
            }
            .mapError { errorLists -> errorLists.flatten() }
    }
}
