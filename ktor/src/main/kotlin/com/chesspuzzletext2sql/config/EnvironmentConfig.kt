package com.chesspuzzletext2sql.config

import com.chesspuzzletext2sql.features.llm.models.LLMConfig
import com.chesspuzzletext2sql.features.llm.models.SupportedModel
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import io.github.cdimascio.dotenv.Dotenv

data class EnvironmentConfig(
    val database: DatabaseConfig,
    val llmConfigs: Map<SupportedModel, LLMConfig>,
    val defaultConfig: SupportedModel,
    val appEnvironment: String = "development",
) {
    data class DatabaseConfig(
        val url: String,
        val driver: String,
        val user: String,
        val password: String,
    )
}

object EnvironmentConfigLoader {
    fun load(dotenv: Dotenv): Result<EnvironmentConfig, Throwable> = runCatching {
        val dbConfig =
            EnvironmentConfig.DatabaseConfig(
                url = dotenv["DB_URL"] ?: "jdbc:h2:mem:test",
                driver = dotenv["DB_DRIVER"] ?: "org.h2.Driver",
                user = dotenv["DB_USER"] ?: "sa",
                password = dotenv["DB_PASSWORD"] ?: "",
            )

        val llmConfigs =
            SupportedModel.entries
                .mapNotNull { model ->
                    val baseUrl = dotenv["LLM_${model.providerName.uppercase()}_BASE_URL"]
                    val apiKey = dotenv["LLM_${model.providerName.uppercase()}_API_KEY"]
                    val modelName = dotenv["LLM_${model.providerName.uppercase()}_MODEL_NAME"]

                    if (baseUrl != null && apiKey != null && modelName != null) {
                        model to LLMConfig(model.providerName, modelName, baseUrl, apiKey)
                    } else {
                        null
                    }
                }
                .toMap()

        val defaultModelName =
            dotenv["DEFAULT_LLM"]
                ?: throw IllegalArgumentException("DEFAULT_LLM environment variable must be set")

        val defaultConfig =
            llmConfigs.entries
                .find { (_, llmConfig) ->
                    llmConfig.provider.equals(defaultModelName, ignoreCase = true)
                }
                ?.key
                ?: throw IllegalArgumentException(
                    "DEFAULT_LLM '$defaultModelName' does not match any configured model. " +
                        "Available models: ${llmConfigs.values.map { it.provider.uppercase() }}"
                )

        EnvironmentConfig(dbConfig, llmConfigs, defaultConfig)
    }
}
