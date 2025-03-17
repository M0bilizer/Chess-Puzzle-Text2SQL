package com.chess.puzzle.text2sql.web.validator

import com.chess.puzzle.text2sql.web.error.ClientError

/**
 * A generic class designed to collect and manage validation errors for requests. It encapsulates a
 * list of [ClientError] objects and provides methods to add errors, check for the presence of
 * errors, and retrieve the error list.
 */
class RequestValidator<T> {
    private val errors = mutableListOf<ClientError>()

    /**
     * Adds a validation error to the list.
     *
     * @param error The [ClientError] object representing the validation error.
     */
    fun addError(error: ClientError) {
        errors.add(error)
    }

    /**
     * Checks if there are any validation errors in the list.
     *
     * @return `true` if there are errors; `false` otherwise.
     */
    fun hasErrors(): Boolean = errors.isNotEmpty()

    /**
     * Retrieves the list of validation errors.
     *
     * @return A list of [ClientError] objects.
     */
    fun getErrors(): List<ClientError> = errors
}

/**
 * A helper class that provides a domain-specific language (DSL) for defining validation rules in a
 * more readable and maintainable way.
 */
class ValidationScope<T>(private val validator: RequestValidator<T>) {

    /**
     * Checks if a given value is not null. If the value is null, adds an error to the validator.
     *
     * @param value The value to validate.
     * @param error The [ClientError] object to add if the value is null.
     */
    fun isNotNull(value: Any?, error: ClientError) {
        if (value == null) {
            validator.addError(error)
        }
    }

    /**
     * Executes a block of validation logic only if the given value is present (not null).
     *
     * @param value The value to check.
     * @param block The validation logic to execute if the value is present.
     */
    fun <V> ifPresent(value: V?, block: ValidationScope<T>.() -> Unit) {
        if (value != null) {
            block()
        }
    }

    /**
     * Verifies if a given value exists in a specified collection. If the value is not in the
     * collection, adds an error to the validator.
     *
     * @param value The value to check.
     * @param collection The collection to check against.
     * @param error The [ClientError] object to add if the value is not in the collection.
     */
    fun <V> isInCollection(value: V?, collection: Collection<V>, error: ClientError) {
        if (value != null && !collection.contains(value)) {
            validator.addError(error)
        }
    }
}

/**
 * A function that creates a new [RequestValidator] instance and configures it using a provided
 * block of code. This allows developers to define validation rules in a concise and expressive
 * manner.
 *
 * @param T The type of data being validated.
 * @param block The validation logic to be executed within the scope.
 * @return A [RequestValidator] instance configured with the provided validation rules.
 */
fun <T> RequestValidator(block: ValidationScope<T>.() -> Unit): RequestValidator<T> {
    val validator = RequestValidator<T>()
    val scope = ValidationScope(validator)
    scope.block()
    return validator
}
