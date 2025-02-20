package com.chess.puzzle.text2sql.web.validator

import com.chess.puzzle.text2sql.web.error.ClientError

class RequestValidator<T> {
    private val errors = mutableListOf<ClientError>()

    fun addError(error: ClientError) {
        errors.add(error)
    }

    fun haveErrors(): Boolean = errors.isNotEmpty()

    fun getErrors(): List<ClientError> = errors
}

class ValidationScope<T>(private val validator: RequestValidator<T>) {
    fun isNotNull(value: Any?, error: ClientError) {
        if (value == null) {
            validator.addError(error)
        }
    }

    fun <V> ifPresent(value: V?, block: ValidationScope<T>.() -> Unit) {
        if (value != null) {
            block()
        }
    }

    fun <V> isInCollection(value: V?, collection: Collection<V>, error: ClientError) {
        if (value != null && !collection.contains(value)) {
            validator.addError(error)
        }
    }
}

fun <T> RequestValidator(block: ValidationScope<T>.() -> Unit): RequestValidator<T> {
    val validator = RequestValidator<T>()
    val scope = ValidationScope(validator)
    scope.block()
    return validator
}
