package com.chesspuzzletext2sql.errors

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result

sealed class ClientError(override val message: String) : CustomError() {
    object InvalidCount : ClientError("Count must be positive")

    object EmptyMessage : ClientError("Message cannot be empty")

    object UnsupportedModel : ClientError("Unsupported model")

    object UnavailableModel : ClientError("Model is unavailable")

    object EmptyQuery : ClientError("Query cannot be empty")

    object InvalidQuery : ClientError("Query is invalid")

    object UnallowedQuery : ClientError("Query is not allowed")

    object EmptyTemplate : ClientError("Template cannot be empty")

    object UnsupportedTemplate : ClientError("Unsupported template")

    data class MultipleErrors(val errors: List<ClientError>) :
        ClientError("Multiple validation errors occurred")
}

class ValidationResult {
    private val errors = mutableListOf<ClientError>()

    fun check(condition: Boolean, error: () -> ClientError) {
        if (!condition) {
            errors.add(error())
        }
    }

    fun <T> requireNotNull(value: T?, error: () -> ClientError): T? {
        if (value == null) {
            errors.add(error())
        }
        return value
    }

    fun <T> toResult(successValue: T): Result<T, ClientError> {
        return when {
            errors.isEmpty() -> Ok(successValue)
            errors.size == 1 -> Err(errors.first())
            else -> Err(ClientError.MultipleErrors(errors))
        }
    }

    fun <T> toResult(successTransform: () -> T): Result<T, ClientError> {
        return when {
            errors.isEmpty() -> Ok(successTransform())
            errors.size == 1 -> Err(errors.first())
            else -> Err(ClientError.MultipleErrors(errors))
        }
    }
}
