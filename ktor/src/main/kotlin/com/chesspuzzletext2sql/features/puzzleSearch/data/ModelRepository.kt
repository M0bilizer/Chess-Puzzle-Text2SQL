package com.chesspuzzletext2sql.features.puzzleSearch.data

import com.chesspuzzletext2sql.features.puzzleSearch.models.LLMConfig
import com.chesspuzzletext2sql.features.puzzleSearch.models.SupportedModel

interface ModelRepository {
    fun getConfig(model: SupportedModel): LLMConfig?

    fun getDefault(): LLMConfig

    fun modelExists(model: SupportedModel): Boolean
}

class ModelRepositoryImp(
    private val modelConfigs: Map<SupportedModel, LLMConfig>,
    private val defaultModel: SupportedModel,
) : ModelRepository {
    init {
        require(defaultModel in modelConfigs.keys) {
            "Default model '$defaultModel' must be present in modelConfigs"
        }
    }

    override fun getConfig(model: SupportedModel) = modelConfigs[model]

    override fun getDefault(): LLMConfig = modelConfigs[defaultModel]!!

    override fun modelExists(model: SupportedModel): Boolean = model in modelConfigs
}
