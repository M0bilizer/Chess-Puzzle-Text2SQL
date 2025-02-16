package com.chess.puzzle.text2sql.web.error

sealed class ClientError {
    abstract val field: String
    abstract val message: String

    data object MissingQuery : ClientError() {
        override val field: String = "query"
        override val message: String = "Query should not be null"
    }

    data object InvalidModel : ClientError() {
        override val field: String = "model"
        override val message: String = "Model is not valid"
    }

    data object InvalidModelVariant : ClientError() {
        override val field: String = "model"
        override val message: String = "Model Variant is not valid"
    }
}
