package com.chess.puzzle.text2sql.web.domain.input

import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.ClientError
import com.chess.puzzle.text2sql.web.error.ClientError.InvalidModelName
import com.chess.puzzle.text2sql.web.error.ClientError.MissingQuery
import com.chess.puzzle.text2sql.web.validator.RequestValidator
import kotlinx.serialization.Serializable

@Serializable
data class Text2SqlRequest(val query: String? = null, val model: String? = null) {
    fun toInput(): ResultWrapper<Text2SqlInput, List<ClientError>> {

        val validator =
            RequestValidator<Text2SqlInput> {
                isNotNull(query, MissingQuery)
                ifPresent(model) { isInCollection(model, ModelName.entries, InvalidModelName) }
            }
        if (validator.haveErrors()) {
            return ResultWrapper.Failure(validator.getErrors())
        }

        val input =
            Text2SqlInput(
                query = query!!,
                model = model?.let { ModelName.toEnum(it) } ?: ModelName.Full,
            )
        return ResultWrapper.Success(input)
    }
}

@Serializable data class Text2SqlInput(val query: String, val model: ModelName)
