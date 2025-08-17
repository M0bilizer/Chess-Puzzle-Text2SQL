package com.chesspuzzletext2sql.plugins

import com.charleskorn.kaml.Yaml
import com.chesspuzzletext2sql.model.AvailableModels
import com.chesspuzzletext2sql.model.AvailablePromptTemplate
import com.chesspuzzletext2sql.model.LLMConfig
import com.chesspuzzletext2sql.model.PromptTemplate
import com.chesspuzzletext2sql.model.SupportedModel
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.Application
import java.io.File
import kotlinx.serialization.decodeFromString

object LLMConfigLoader {
  private const val ENV_PREFIX = "LLM_"

  fun load() {
    val loaded =
      SupportedModel.all
        .mapNotNull { model ->
          try {
            model to loadConfig(model).also { println("✅ Loaded ${model.providerName}") }
          } catch (e: Exception) {
            println("⚠️ Skipped ${model.providerName}: ${e.message}")
            null
          }
        }
        .toMap()
    AvailableModels.update(loaded)
  }

  private fun loadConfig(model: SupportedModel): LLMConfig {
    val prefix = "${ENV_PREFIX}${model.providerName.uppercase()}_"

    return LLMConfig(
      provider = model.providerName,
      baseUrl = getEnvOrThrow("${prefix}BASE_URL"),
      apiKey = getEnvOrThrow("${prefix}API_KEY"),
      modelName = getEnvOrThrow("${prefix}MODEL_NAME"),
    )
  }

  private fun getEnvOrThrow(key: String): String {
    val dotenv = dotenv()
    return dotenv[key] ?: throw IllegalStateException("Missing env var: $key")
  }
}

object PromptTemplateConfigLoader {
  private val yaml = Yaml.default

  fun load() {
    val configFile = File("prompt-templates.yaml").readText()
    val loaded = yaml.decodeFromString<Map<String, PromptTemplate>>(configFile)
    AvailablePromptTemplate.update(loaded)
  }
}

fun Application.configureConfigLoader() {
  LLMConfigLoader.load()
  PromptTemplateConfigLoader.load()
}
