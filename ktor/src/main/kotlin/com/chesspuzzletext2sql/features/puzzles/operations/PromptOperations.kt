package com.chesspuzzletext2sql.features.puzzles.operations

import com.chesspuzzletext2sql.features.puzzles.domains.PromptTemplate
import com.chesspuzzletext2sql.shared.data.repositories.TemplateRepository
import com.chesspuzzletext2sql.shared.errors.ApplicationError
import com.chesspuzzletext2sql.shared.errors.NoTemplateFound
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
