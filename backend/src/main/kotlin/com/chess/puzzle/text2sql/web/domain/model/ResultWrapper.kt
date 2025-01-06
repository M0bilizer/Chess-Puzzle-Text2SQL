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
     * Represents a successful result of a function, encapsulating the relevant data.
     *
     * This class is used to return data when an operation is successful.
     *
     * @sample Success(data = "SELECT * FROM t_puzzle WHERE opening_tags LIKE '%Italian_Defense%'")
     */
    data class Success<out T>(val data: T) : ResultWrapper<T, Nothing>()

    /**
     * Represents a failure that occurs during an operation.
     *
     * This class is used to inform callers about failure that occur during operations.
     */
    data class Failure<out E>(val error: E) : ResultWrapper<Nothing, E>()
}
