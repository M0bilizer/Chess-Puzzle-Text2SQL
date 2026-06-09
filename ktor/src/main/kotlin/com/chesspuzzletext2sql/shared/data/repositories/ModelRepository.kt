package com.chesspuzzletext2sql.shared.data.repositories

import com.chesspuzzletext2sql.features.puzzles.domains.LLMConfig
import com.chesspuzzletext2sql.features.puzzles.domains.SupportedModel

class ModelRepository(
    private val modelConfigs: Map<SupportedModel, LLMConfig>,
    private val defaultModel: SupportedModel,
) {
    init {
        require(defaultModel in modelConfigs.keys) {
            "Default model '$defaultModel' must be present in modelConfigs"
        }
    }

    fun getConfig(model: SupportedModel) = modelConfigs[model]

    fun getDefault(): LLMConfig = modelConfigs[defaultModel]!!

    fun modelExists(model: SupportedModel): Boolean = model in modelConfigs
}
