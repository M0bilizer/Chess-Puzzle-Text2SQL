package com.chess.puzzle.text2sql.web.entities.helper

sealed class ResultWrapper<T> {
    data class Success<T>(val data: T) : ResultWrapper<T>()

    sealed class Error : ResultWrapper<Nothing>() {
        data class ValidationError(val isValid: Boolean, val isAllowed: Boolean) : Error()

        data class HibernateError(val message: String?) : Error()

        data object ResponseError : Error()
    }
}
