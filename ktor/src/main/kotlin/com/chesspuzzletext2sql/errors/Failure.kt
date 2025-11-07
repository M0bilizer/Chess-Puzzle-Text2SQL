package com.chesspuzzletext2sql.errors

sealed interface Failure {
    val type: FailureType
}

sealed interface FailureType {
    val code: String
}
