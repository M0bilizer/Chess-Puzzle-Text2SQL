package com.chess.puzzle.text2sql.web.entities.helper

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
sealed class ResultWrapper<T> {
    /**
     * Represents a successful result of a function, encapsulating the relevant data.
     *
     * This class is used to return data when an operation is successful.
     *
     * @sample Success(data = "SELECT * FROM t_puzzle WHERE opening_tags LIKE '%Italian_Defense%'")
     */
    data class Success<T>(val data: T) : ResultWrapper<T>()

    /**
     * Represents an error that occurs during an operation.
     *
     * This class is used to inform callers about errors that occur during operations.
     */
    sealed class Error : ResultWrapper<Nothing>() {
        /**
         * Represents a sql validation error that occurs before a query on the database is
         * performed.
         *
         * This class is used to inform callers about validation issues, on whether the SQL
         * statement is valid or if it is allowed.
         *
         * @see [com.chess.puzzle.text2sql.web.validator.SqlValidator]
         */
        data class ValidationError(val isValid: Boolean, val isAllowed: Boolean) : Error()

        /**
         * Represents a Hibernate error that occurs during an operation.
         *
         * This class is used to inform callers about Hibernate-related errors, such as database
         * access issues.
         */
        data class HibernateError(val message: String?) : Error()

        /**
         * Represents a response error that occurs during an operation.
         *
         * This class is used to inform callers about response-related errors, such as HTTP response
         * issues.
         */
        data object ResponseError : Error()
    }
}
