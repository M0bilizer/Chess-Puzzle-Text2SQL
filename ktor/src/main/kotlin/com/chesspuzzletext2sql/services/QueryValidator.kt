package com.chesspuzzletext2sql.services

import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.InvalidRequestDetail
import com.chesspuzzletext2sql.errors.InvalidRequestMessage
import com.chesspuzzletext2sql.helpers.mapViolationsToErrors
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import dev.nesk.akkurate.ValidationResult
import dev.nesk.akkurate.Validator
import io.ktor.http.Parameters
import io.ktor.server.routing.RoutingContext

data class QueryValidationConfig<A : Any, C>(
    val validator: Validator.Runner<A>,
    val transform: (A) -> C,
    val parser: (Parameters) -> A,
)

class QueryValidator {
    // Removed json parameter since it's not used for query parameters

    inline fun <reified A : Any, C> validateQuery(
        context: RoutingContext, // Added context parameter
        validationConfig: QueryValidationConfig<A, C>,
    ): Result<C, Fail> {
        // Step 1: Parse query parameters
        val requestResult = parseQueryParameters(context, validationConfig.parser) // Pass context
        if (requestResult.isErr) return Err(requestResult.error)

        // Step 2: Validate business logic
        return validateBusinessLogic(requestResult.value, validationConfig.validator).map {
            validationConfig.transform(it)
        }
    }

    fun <A> parseQueryParameters(
        context: RoutingContext, // Added context parameter
        parser: (Parameters) -> A,
    ): Result<A, Fail> {
        return try {
            Ok(parser(context.call.parameters)) // Use context.call.parameters
        } catch (e: Exception) {
            Err(handleQueryParsingError(e))
        }
    }

    private fun handleQueryParsingError(e: Exception): Fail {
        return when (e) {
            is NumberFormatException ->
                Fail.InvalidRequest(
                    listOf(
                        InvalidRequestDetail(
                            "query_param",
                            InvalidRequestMessage.TypeMismatch(
                                "query_param",
                                "Invalid number format",
                                "Valid number",
                            ),
                        )
                    )
                )

            is IllegalArgumentException ->
                Fail.InvalidRequest(
                    listOf(
                        InvalidRequestDetail(
                            "query_param",
                            InvalidRequestMessage.TypeMismatch(
                                "query_param",
                                e.message ?: "Invalid value",
                                "Valid value",
                            ),
                        )
                    )
                )

            else ->
                Fail.InvalidRequest(
                    listOf(
                        InvalidRequestDetail(
                            "query_param",
                            InvalidRequestMessage.TypeMismatch(
                                "query_param",
                                "Failed to parse query parameters",
                                "Valid query parameters",
                            ),
                        )
                    )
                )
        }
    }

    fun <A> validateBusinessLogic(request: A, validator: Validator.Runner<A>): Result<A, Fail> {
        return when (val result = validator(request)) {
            is ValidationResult.Success -> Ok(result.value)
            is ValidationResult.Failure -> Err(mapViolationsToErrors(result.violations))
        }
    }
}

// Extension function for easy usage
inline fun <reified A : Any, C> RoutingContext.validateQuery(
    validationConfig: QueryValidationConfig<A, C>
): Result<C, Fail> {
    val queryValidator = QueryValidator()
    return queryValidator.validateQuery(this, validationConfig) // Pass 'this' (RoutingContext)
}

object QueryParsers {
    // Basic string parser
    fun stringParser(paramName: String, default: String? = null): (Parameters) -> String =
        { params ->
            params[paramName]
                ?: default
                ?: throw IllegalArgumentException("Missing required parameter: $paramName")
        }

    // Int parser
    fun intParser(paramName: String, default: Int? = null): (Parameters) -> Int = { params ->
        params[paramName]?.toIntOrNull()
            ?: default
            ?: throw IllegalArgumentException("Missing or invalid parameter: $paramName")
    }

    // Boolean parser
    fun booleanParser(paramName: String, default: Boolean? = null): (Parameters) -> Boolean =
        { params ->
            params[paramName]?.toBooleanStrictOrNull()
                ?: default
                ?: throw IllegalArgumentException("Missing or invalid parameter: $paramName")
        }

    // Enum parser
    inline fun <reified T : Enum<T>> enumParser(
        paramName: String,
        default: T? = null,
    ): (Parameters) -> T = { params ->
        params[paramName]?.let { value ->
            enumValues<T>().find { it.name.equals(value, ignoreCase = true) }
        } ?: default ?: throw IllegalArgumentException("Missing or invalid parameter: $paramName")
    }

    // Optional parameter parser
    fun optionalStringParser(paramName: String): (Parameters) -> String? = { params ->
        params[paramName]
    }
}
