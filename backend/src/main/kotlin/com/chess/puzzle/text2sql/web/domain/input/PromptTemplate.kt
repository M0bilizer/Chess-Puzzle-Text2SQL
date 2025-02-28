package com.chess.puzzle.text2sql.web.domain.input

import com.chess.puzzle.text2sql.web.domain.model.ModelVariant
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.ClientError
import com.chess.puzzle.text2sql.web.error.ClientError.InvalidModelVariant
import com.chess.puzzle.text2sql.web.error.ClientError.MissingQuery
import com.chess.puzzle.text2sql.web.validator.RequestValidator
import kotlinx.serialization.Serializable

@Serializable
data class PromptTemplateRequest(val query: String? = null, val variant: String? = null) {
    fun toInput(): ResultWrapper<PromptTemplateInput, List<ClientError>> {

        val validator =
            RequestValidator<PromptTemplateInput> {
                isNotNull(query, MissingQuery)
                ifPresent(variant) {
                    isNotNull(ModelVariant.toEnum(variant!!), InvalidModelVariant)
                }
            }
        if (validator.haveErrors()) {
            return ResultWrapper.Failure(validator.getErrors())
        }

        val input =
            PromptTemplateInput(
                query = query!!,
                modelVariant = variant?.let { ModelVariant.toEnum(it) } ?: ModelVariant.Full,
            )
        return ResultWrapper.Success(input)
    }
}

@Serializable data class PromptTemplateInput(val query: String, val modelVariant: ModelVariant)
