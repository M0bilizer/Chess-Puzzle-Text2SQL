package com.chesspuzzletext2sql.features.llm.core

import com.chesspuzzletext2sql.features.llm.data.ModelRepository
import com.chesspuzzletext2sql.features.llm.models.LLMConfig
import com.chesspuzzletext2sql.features.llm.models.SupportedModel
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result

fun getModelConfig(model: String?, repository: ModelRepository): Result<LLMConfig, Throwable> =
    when (model) {
        null -> Ok(repository.getDefault())
        is String ->
            runCatching {
                    val supportedModel =
                        SupportedModel.fromProviderName(model.uppercase())
                            ?: throw IllegalArgumentException("Model not found")
                    repository.getConfig(supportedModel)
                }
                .fold(onSuccess = { result -> result }, onFailure = { throwable -> Err(throwable) })
    }
