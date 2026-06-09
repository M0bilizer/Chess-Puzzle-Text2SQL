package com.chesspuzzletext2sql.shared.config

import com.chesspuzzletext2sql.features.puzzles.domains.LLMConfig
import com.chesspuzzletext2sql.features.puzzles.domains.SupportedModel
import com.chesspuzzletext2sql.shared.errors.StartupError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.cdimascio.dotenv.Dotenv

// --- Type Aliases for Clarity ---
typealias ConfigKey = String

typealias ConfigValue = String

typealias ConfigValues = Map<ConfigKey, ConfigValue>

typealias ConfigErrors = List<StartupError>

typealias ConfigResult = Pair<ConfigValues, ConfigErrors>

data class EnvironmentConfig(
    val database: DatabaseConfig,
    val llmConfigs: Map<SupportedModel, LLMConfig>,
    val defaultConfig: SupportedModel,
) {
    data class DatabaseConfig(
        val url: String,
        val driver: String,
        val user: String,
        val password: String,
    )
}

object EnvironmentConfigLoader {
    private fun getRequired(dotenv: Dotenv, key: ConfigKey): Result<ConfigValue, StartupError> =
        dotenv[key]?.takeIf { it.isNotBlank() }?.let { Ok(it) }
            ?: Err(StartupError("$key is missing"))

    private fun collectResults(
        results: List<Pair<ConfigKey, Result<ConfigValue, StartupError>>>
    ): ConfigResult {
        val values = mutableMapOf<ConfigKey, ConfigValue>()
        val errors = mutableListOf<StartupError>()

        results.forEach { (key, result) ->
            result.fold(success = { values[key] = it }, failure = { errors.add(it) })
        }

        return values to errors
    }

    fun load(dotenv: Dotenv): Result<EnvironmentConfig, List<StartupError>> {
        val errors = mutableListOf<StartupError>()

        // Load database config
        val dbResults =
            listOf(
                "DB_URL" to getRequired(dotenv, "DB_URL"),
                "DB_DRIVER" to getRequired(dotenv, "DB_DRIVER"),
                "DB_USER" to getRequired(dotenv, "DB_USER"),
                "DB_PASSWORD" to getRequired(dotenv, "DB_PASSWORD"),
            )
        val (dbValues, dbErrors) = collectResults(dbResults)
        errors.addAll(dbErrors)

        // Load LLM configs
        val llmConfigs =
            SupportedModel.entries
                .mapNotNull { model ->
                    val provider = model.providerName.uppercase()

                    val modelName = dotenv["LLM_${provider}_MODEL_NAME"]?.takeIf { it.isNotBlank() }
                    val baseUrl = dotenv["LLM_${provider}_BASE_URL"]?.takeIf { it.isNotBlank() }
                    val apiKey = dotenv["LLM_${provider}_API_KEY"]?.takeIf { it.isNotBlank() }

                    if (modelName != null && baseUrl != null && apiKey != null) {
                        model to LLMConfig(model.providerName, modelName, baseUrl, apiKey)
                    } else {
                        null
                    }
                }
                .toMap()

        if (llmConfigs.isEmpty()) {
            errors.add(StartupError("No LLM configuration found"))
        }

        // Load default model
        val defaultModelResult = getRequired(dotenv, "DEFAULT_LLM")
        val defaultModel =
            defaultModelResult.fold(
                success = { defaultModelName ->
                    llmConfigs.entries
                        .find { (_, config) ->
                            config.provider.equals(defaultModelName, ignoreCase = true)
                        }
                        ?.key
                        ?: run {
                            errors.add(
                                StartupError(
                                    "DEFAULT_LLM '$defaultModelName' does not match any configured model. " +
                                        "Available models: ${llmConfigs.values.map { it.provider.uppercase() }}"
                                )
                            )
                            null
                        }
                },
                failure = { error ->
                    errors.add(error)
                    null
                },
            )

        return if (errors.isEmpty() && dbValues.size == 4 && defaultModel != null) {
            Ok(
                EnvironmentConfig(
                    EnvironmentConfig.DatabaseConfig(
                        url = dbValues["DB_URL"]!!,
                        driver = dbValues["DB_DRIVER"]!!,
                        user = dbValues["DB_USER"]!!,
                        password = dbValues["DB_PASSWORD"]!!,
                    ),
                    llmConfigs,
                    defaultModel,
                )
            )
        } else {
            Err(errors)
        }
    }
}
