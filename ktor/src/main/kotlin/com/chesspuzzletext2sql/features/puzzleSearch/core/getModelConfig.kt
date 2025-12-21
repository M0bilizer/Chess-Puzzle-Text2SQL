package com.chesspuzzletext2sql.features.puzzleSearch.core

import com.chesspuzzletext2sql.errors.ApplicationError
import com.chesspuzzletext2sql.errors.NoModelConfigFound
import com.chesspuzzletext2sql.errors.UnsupportedModel
import com.chesspuzzletext2sql.features.puzzleSearch.data.ModelRepository
import com.chesspuzzletext2sql.features.puzzleSearch.models.LLMConfig
import com.chesspuzzletext2sql.features.puzzleSearch.models.SupportedModel
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result

fun getModelConfig(
    model: String?,
    repository: ModelRepository,
): Result<LLMConfig, ApplicationError> =
    when (model) {
        null -> Ok(repository.getDefault())
        is String -> {
            val supportedModel =
                SupportedModel.fromProviderName(model) ?: return Err(UnsupportedModel(model))
            val result =
                repository.getConfig(supportedModel) ?: return Err(NoModelConfigFound(model))
            Ok(result)
        }
    }
