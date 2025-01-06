package com.chess.puzzle.text2sql.web.error

sealed class ClientError {
    abstract val field: String
    abstract val message: String

    data object MissingQuery : ClientError() {
        override val field: String = "query"
        override val message: String = "Query should not be null"
    }

    data object InvalidModelName : ClientError() {
        override val field: String = "modelName"
        override val message: String = "Model Name is not valid"
    }
}
