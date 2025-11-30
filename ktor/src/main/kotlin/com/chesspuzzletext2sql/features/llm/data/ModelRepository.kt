package com.chesspuzzletext2sql.features.llm.data

import com.chesspuzzletext2sql.features.llm.models.LLMConfig
import com.chesspuzzletext2sql.features.llm.models.SupportedModel
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching

interface ModelRepository {
    fun getConfig(model: SupportedModel): Result<LLMConfig, Throwable>

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

    override fun getConfig(model: SupportedModel): Result<LLMConfig, Throwable> = runCatching {
        modelConfigs[model] ?: throw NoSuchElementException("Model '$model' not configured")
    }

    override fun getDefault(): LLMConfig = modelConfigs[defaultModel]!!

    override fun modelExists(model: SupportedModel): Boolean = model in modelConfigs
}
