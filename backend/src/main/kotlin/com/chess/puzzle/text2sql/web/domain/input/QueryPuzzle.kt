package com.chess.puzzle.text2sql.web.domain.input

import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ModelVariant
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.ClientError
import com.chess.puzzle.text2sql.web.error.ClientError.InvalidModel
import com.chess.puzzle.text2sql.web.error.ClientError.MissingQuery
import com.chess.puzzle.text2sql.web.utility.CustomLogger
import com.chess.puzzle.text2sql.web.validator.RequestValidator
import kotlinx.serialization.Serializable

private val customLogger = CustomLogger(indentLevel = 1)

@Serializable
data class QueryPuzzleRequest(val query: String? = null, val model: String? = null) {
    fun toInput(): ResultWrapper<QueryPuzzleInput, List<ClientError>> {
        customLogger.init { "QueryPuzzleRequest(query=$query, model=$model).toInput())" }
        val validator =
            RequestValidator<QueryPuzzleInput> {
                isNotNull(query, MissingQuery)
                ifPresent(model) {
                    isNotNull(ModelName.toEnum(model!!), InvalidModel)
                    isInCollection(ModelVariant.toEnum(model), ModelVariant.entries, InvalidModel)
                }
            }
        if (validator.haveErrors()) {
            customLogger.error{
                "Client Errors : ${validator.getErrors()}"
            }
            return ResultWrapper.Failure(validator.getErrors())
        }

        val input =
            QueryPuzzleInput(
                query = query!!,
                modelName = model?.let { ModelName.toEnum(it) } ?: ModelName.Deepseek,
            )
        return ResultWrapper.Success(input)
    }
}

@Serializable data class QueryPuzzleInput(val query: String, val modelName: ModelName)
