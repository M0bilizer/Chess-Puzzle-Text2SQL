package com.chess.puzzle.text2sql.web.domain.model

/**
 * Represents the result of a function, encapsulating either success with data or an error.
 *
 * This class is used to inform callers about the success or failure of operations. Generally, all
 * services and helpers will need to inform their caller if their operation is successful:
 * - If yes, return the relevant data.
 * - If no, return the exception.
 *
 * This class is type-agnostic.
 *
 * @see [com.chess.puzzle.text2sql.web.service]
 */
sealed class ResultWrapper<out T, out E> {
    /**
     * Represents a successful result of a function, encapsulating the relevant data and optional
     * metadata.
     *
     * This class is used to return data when an operation is successful. The optional metadata
     * parameter allows additional information to be associated with the result.
     *
     * @Sample Success(data = "SELECT * FROM t_puzzle WHERE opening_tags LIKE '%Italian_Defense%'")
     */
    data class Success<out T>(val data: T, val metadata: Any?) : ResultWrapper<T, Nothing>() {
        /**
         * Creates a Success instance with the provided data, setting metadata to null.
         *
         * @param data The relevant data returned by the operation.
         */
        constructor(data: T) : this(data, null)
    }

    /**
     * Represents a failure that occurs during an operation.
     *
     * This class is used to inform callers about failure that occur during operations.
     */
    data class Failure<out E>(val error: E) : ResultWrapper<Nothing, E>()
}
