package com.chess.puzzle.text2sql.web.domain.input

import com.chess.puzzle.text2sql.web.domain.model.ModelVariant
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.ClientError
import com.chess.puzzle.text2sql.web.error.ClientError.InvalidModelVariant
import com.chess.puzzle.text2sql.web.error.ClientError.MissingQuery
import com.chess.puzzle.text2sql.web.validator.RequestValidator
import kotlinx.serialization.Serializable

/**
 * Represents a request for generating a prompt template using a specified model variant.
 *
 * This class encapsulates the input data required for generating a prompt template. The `query` and
 * `variant` fields are optional, but validation is performed to ensure they meet the required
 * criteria before processing.
 *
 * @property query The input query string to be used for generating the prompt template. This field
 *   is optional but must be non-null for the request to be valid.
 * @property variant The variant of the model to be used for generating the prompt template. This
 *   field is optional and defaults to `null`. If provided, it must be a valid [ModelVariant].
 */
@Serializable
data class PromptTemplateRequest(val query: String? = null, val variant: String? = null) {

    /**
     * Converts the [PromptTemplateRequest] into a validated [PromptTemplateInput] object.
     *
     * This method performs validation on the `query` and `variant` fields:
     * - The `query` field must be non-null.
     * - If the `variant` field is present, it must be a valid [ModelVariant].
     *
     * If validation fails, a [ResultWrapper.Failure] is returned containing a list of
     * [ClientError]s. If validation succeeds, a [ResultWrapper.Success] is returned containing the
     * validated [PromptTemplateInput].
     *
     * @return A [ResultWrapper] containing either the validated [PromptTemplateInput] or a list of
     *   validation errors.
     */
    fun toInput(): ResultWrapper<PromptTemplateInput, List<ClientError>> {

        val validator =
            RequestValidator<PromptTemplateInput> {
                isNotNull(query, MissingQuery)
                ifPresent(variant) {
                    isNotNull(ModelVariant.toEnum(variant!!), InvalidModelVariant)
                }
            }
        if (validator.hasErrors()) {
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

/**
 * Represents the validated input for generating a prompt template.
 *
 * This class is created after validating a [PromptTemplateRequest]. It contains the non-null
 * `query` and the validated `modelVariant` to be used for generating the prompt template.
 *
 * @property query The non-null query string to be used for generating the prompt template.
 * @property modelVariant The validated model variant to be used for generating the prompt template.
 *   Defaults to [ModelVariant.Full] if no variant is specified in the request.
 */
@Serializable data class PromptTemplateInput(val query: String, val modelVariant: ModelVariant)
