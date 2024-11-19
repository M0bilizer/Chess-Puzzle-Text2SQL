package com.chess.puzzle.text2sql.web.helper

import com.chess.puzzle.text2sql.web.entities.Puzzle

sealed class ResultWrapper {
    data class PuzzleDataSuccess(val data: List<Puzzle>) : ResultWrapper()

    data class DemonstrationDataSuccess(val data: List<Demonstration>) : ResultWrapper()

    data class Success(val message: String) : ResultWrapper()

    data class ValidationError(val isValid: Boolean, val isAllowed: Boolean) : ResultWrapper()

    data class HibernateError(val message: String?) : ResultWrapper()

    data object ResponseError : ResultWrapper()
}
