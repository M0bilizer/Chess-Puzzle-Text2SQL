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

/**
 * Represents a request for converting text to SQL, optionally specifying the model and its variant.
 *
 * This class handles the validation and conversion of the input parameters to a validated
 * [Text2SqlInput]. It ensures that the provided query, model, and model variant meet the required
 * criteria before conversion.
 *
 * @property query The text query to be converted to SQL. Nullable in the request but validated to
 *   be non-null.
 * @property model The optional name of the model to use for the conversion. Validated against
 *   allowed values if present.
 * @property modelVariant The optional variant of the model to use. Validated against allowed values
 *   if present.
 */
@Serializable
data class Text2SqlRequest(
    val query: String? = null,
    val model: String? = null,
    val modelVariant: String? = null,
) {

    /**
     * Validates and converts the request to a validated [Text2SqlInput].
     *
     * Performs the following validations:
     * - Ensures [query] is not null.
     * - Validates [model] against allowed [ModelName] values if present.
     * - Validates [modelVariant] against allowed [ModelVariant] values if present.
     *
     * @return A [ResultWrapper] containing either:
     *     - [ResultWrapper.Success] with the validated [Text2SqlInput] if validation passes.
     *     - [ResultWrapper.Failure] with a list of [ClientError]s if validation fails.
     *
     * @see ModelName
     * @see ModelVariant
     * @see ClientError
     */
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

/**
 * Represents validated input parameters for a text-to-SQL conversion operation.
 *
 * This class contains non-nullable and validated properties ready for use in the conversion
 * process. The properties are derived from [Text2SqlRequest] after successful validation.
 *
 * @property query The validated non-null text query to be converted to SQL.
 * @property modelName The parsed and validated model name, defaults to [ModelName.Deepseek] if not
 *   specified in the request.
 * @property modelVariant The parsed and validated model variant, defaults to [ModelVariant.Full] if
 *   not specified in the request.
 */
@Serializable
data class Text2SqlInput(
    val query: String,
    val modelName: ModelName,
    val modelVariant: ModelVariant,
)
