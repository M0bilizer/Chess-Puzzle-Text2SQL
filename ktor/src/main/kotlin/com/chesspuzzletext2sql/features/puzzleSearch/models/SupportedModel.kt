package com.chesspuzzletext2sql.features.puzzleSearch.models

sealed class SupportedModel {
    abstract val providerName: String

    data object DeepSeek : SupportedModel() {
        override val providerName = "deepseek"
    }

    data object Mistral : SupportedModel() {
        override val providerName = "mistral"
    }

    companion object {
        internal val entries = setOf(DeepSeek, Mistral)

        fun fromProviderName(name: String): SupportedModel? =
            entries.find { it.providerName == name.lowercase() }
    }
}
