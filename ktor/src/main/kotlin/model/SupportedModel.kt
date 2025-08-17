package com.chesspuzzletext2sql.model

sealed class SupportedModel {
  abstract val providerName: String

  data object DeepSeek : SupportedModel() {
    override val providerName = "deepseek"
  }

  data object Mistral : SupportedModel() {
    override val providerName = "mistral"
  }

  companion object {
    internal val all = setOf(DeepSeek, Mistral)

    fun fromProviderName(name: String): SupportedModel? =
      all.find { it.providerName == name.lowercase() }
  }
}

object AvailableModels {
  private val storage = mutableMapOf<SupportedModel, LLMConfig>()

  operator fun get(model: SupportedModel): LLMConfig? = storage[model]

  val all: Map<SupportedModel, LLMConfig>
    get() = storage.toMap()

  internal fun update(configs: Map<SupportedModel, LLMConfig>) {
    require(configs.isNotEmpty()) { "Must have at least one available model" }
    storage.clear()
    storage.putAll(configs)
  }
}
