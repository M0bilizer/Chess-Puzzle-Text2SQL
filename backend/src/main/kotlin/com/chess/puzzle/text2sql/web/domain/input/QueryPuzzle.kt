package com.chess.puzzle.text2sql.web.domain.input

import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ModelVariant
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.ClientError
import com.chess.puzzle.text2sql.web.error.ClientError.InvalidModel
import com.chess.puzzle.text2sql.web.error.ClientError.MissingQuery
import com.chess.puzzle.text2sql.web.validator.RequestValidator
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable

private val logger = KotlinLogging.logger {}

/**
 * Represents a request to query a puzzle, potentially specifying a model to use.
 *
 * This class handles validation and conversion to a validated input format. Both parameters are
 * optional in the request but are validated during conversion.
 *
 * @property query The search query string. Nullable in the request but validated to be non-null.
 * @property model Optional model name string to use for the query. Validated against allowed values
 *   if present.
 */
@Serializable
data class QueryPuzzleRequest(val query: String? = null, val model: String? = null) {

    /**
     * Validates and converts the request to a validated [QueryPuzzleInput].
     *
     * Performs validation checks:
     * - Ensures [query] is not null
     * - If [model] is present, validates it matches allowed [ModelName] and [ModelVariant] values
     *
     * @return [ResultWrapper.Success] with [QueryPuzzleInput] if validation passes,
     *   [ResultWrapper.Failure] with list of [ClientError]s if validation fails
     * @see ModelName
     * @see ModelVariant
     * @see ClientError
     */
    fun toInput(): ResultWrapper<QueryPuzzleInput, List<ClientError>> {
        val validator =
            RequestValidator<QueryPuzzleInput> {
                isNotNull(query, MissingQuery)
                ifPresent(model) {
                    isNotNull(ModelName.toEnum(model!!), InvalidModel)
                    isInCollection(ModelVariant.toEnum(model), ModelVariant.entries, InvalidModel)
                }
            }
        if (validator.haveErrors()) {
            logger.error {
                "ERROR: Client Errors: ${validator.getErrors()} <- QueryPuzzleRequest(query=$query, model=$model).toInput())"
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

/**
 * Represents validated input parameters for a puzzle query operation.
 *
 * This is the validated version of [QueryPuzzleRequest] with non-optional parameters and properly
 * parsed model information.
 *
 * @property query The validated non-null search query string
 * @property modelName The parsed model name, defaults to [ModelName.Deepseek] if not specified in
 *   request
 */
@Serializable data class QueryPuzzleInput(val query: String, val modelName: ModelName)
