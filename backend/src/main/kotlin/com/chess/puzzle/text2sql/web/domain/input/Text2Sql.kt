package com.chess.puzzle.text2sql.web.domain.input

import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ModelVariant
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.ClientError
import com.chess.puzzle.text2sql.web.error.ClientError.InvalidModel
import com.chess.puzzle.text2sql.web.error.ClientError.InvalidModelVariant
import com.chess.puzzle.text2sql.web.error.ClientError.MissingQuery
import com.chess.puzzle.text2sql.web.validator.RequestValidator
import kotlinx.serialization.Serializable

@Serializable
data class Text2SqlRequest(
    val query: String? = null,
    val model: String? = null,
    val modelVariant: String? = null,
) {
    fun toInput(): ResultWrapper<Text2SqlInput, List<ClientError>> {

        val validator =
            RequestValidator<Text2SqlRequest> {
                isNotNull(query, MissingQuery)
                ifPresent(model) {
                    isNotNull(ModelName.toEnum(model!!), InvalidModel)
                    isInCollection(ModelName.toEnum(model), ModelName.entries, InvalidModel)
                }
                ifPresent(modelVariant) {
                    isNotNull(ModelVariant.toEnum(modelVariant!!), InvalidModelVariant)
                    isInCollection(
                        ModelVariant.toEnum(modelVariant),
                        ModelVariant.entries,
                        InvalidModelVariant,
                    )
                }
            }
        if (validator.haveErrors()) {
            return ResultWrapper.Failure(validator.getErrors())
        }

        val input =
            Text2SqlInput(
                query = query!!,
                modelName = model?.let { ModelName.toEnum(it) } ?: ModelName.Deepseek,
                modelVariant = modelVariant?.let { ModelVariant.toEnum(it) } ?: ModelVariant.Full,
            )
        return ResultWrapper.Success(input)
    }
}

@Serializable
data class Text2SqlInput(
    val query: String,
    val modelName: ModelName,
    val modelVariant: ModelVariant,
)
