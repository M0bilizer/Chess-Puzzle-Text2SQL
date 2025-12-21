package com.chesspuzzletext2sql.features.puzzleSearch.core

import com.chesspuzzletext2sql.errors.ApplicationError
import com.chesspuzzletext2sql.errors.NoTemplateFound
import com.chesspuzzletext2sql.features.puzzleSearch.data.TemplateRepository
import com.chesspuzzletext2sql.features.puzzleSearch.models.PromptTemplate
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result

fun getPromptTemplate(
    template: String?,
    repository: TemplateRepository,
): Result<PromptTemplate, ApplicationError> =
    when (template) {
        null -> Ok(repository.getDefault())
        is String -> {
            val result = repository.getTemplate(template) ?: return Err(NoTemplateFound(template))
            Ok(result)
        }
    }
