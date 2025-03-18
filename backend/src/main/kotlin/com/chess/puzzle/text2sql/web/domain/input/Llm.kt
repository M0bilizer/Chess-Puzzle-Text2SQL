package com.chess.puzzle.text2sql.web.domain.input

import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ModelVariant
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.ClientError
import com.chess.puzzle.text2sql.web.error.ClientError.InvalidModel
import com.chess.puzzle.text2sql.web.error.ClientError.MissingQuery
import com.chess.puzzle.text2sql.web.validator.RequestValidator
import kotlinx.serialization.Serializable

/**
 * Represents a request to the LLM (Large Language Model) service.
 *
 * This class encapsulates the input data required for processing a query using a specified model.
 * The `query` and `model` fields are optional, but validation is performed to ensure they meet the
 * required criteria before processing.
 *
 * @property query The input query string to be processed by the LLM. This field is optional but
 *   must be non-null for the request to be valid.
 * @property model The name of the model to be used for processing the query. This field is optional
 *   and defaults to `null`. If provided, it must be a valid [ModelName] and [ModelVariant].
 */
@Serializable
data class LlmRequest(val query: String? = null, val model: String? = null) {

    /**
     * Converts the [LlmRequest] into a validated [LlmInput] object.
     *
     * This method performs validation on the `query` and `model` fields:
     * - The `query` field must be non-null.
     * - If the `model` field is present, it must be a valid [ModelName] and [ModelVariant].
     *
     * If validation fails, a [ResultWrapper.Failure] is returned containing a list of
     * [ClientError]s. If validation succeeds, a [ResultWrapper.Success] is returned containing the
     * validated [LlmInput].
     *
     * @return A [ResultWrapper] containing either the validated [LlmInput] or a list of validation
     *   errors.
     */
    fun toInput(): ResultWrapper<LlmInput, List<ClientError>> {

        val validator =
            RequestValidator<LlmInput> {
                isNotNull(query, MissingQuery)
                ifPresent(model) {
                    isNotNull(ModelName.toEnum(model!!), InvalidModel)
                    isInCollection(ModelVariant.toEnum(model), ModelVariant.entries, InvalidModel)
                }
            }
        if (validator.hasErrors()) {
            return ResultWrapper.Failure(validator.getErrors())
        }

        val input =
            LlmInput(
                query = query!!,
                modelName = model?.let { ModelName.toEnum(it) } ?: ModelName.Deepseek,
            )
        return ResultWrapper.Success(input)
    }
}

/**
 * Represents the validated input for processing a query using an LLM.
 *
 * This class is created after validating an [LlmRequest]. It contains the non-null `query` and the
 * validated `modelName` to be used for processing.
 *
 * @property query The non-null query string to be processed by the LLM.
 * @property modelName The validated model to be used for processing the query. Defaults to
 *   [ModelName.Deepseek] if no model is specified in the request.
 */
@Serializable data class LlmInput(val query: String, val modelName: ModelName)
