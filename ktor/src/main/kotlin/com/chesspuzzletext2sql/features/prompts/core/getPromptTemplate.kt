package com.chesspuzzletext2sql.features.prompts.core

import com.chesspuzzletext2sql.features.prompts.data.TemplateRepository
import com.chesspuzzletext2sql.features.prompts.models.PromptTemplate
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result

fun getModelConfig(
    template: String?,
    repository: TemplateRepository,
): Result<PromptTemplate, Throwable> =
    when (template) {
        null -> Ok(repository.getDefault())
        is String ->
            runCatching { repository.getTemplate(template) }
                .fold(onSuccess = { result -> result }, onFailure = { throwable -> Err(throwable) })
    }
