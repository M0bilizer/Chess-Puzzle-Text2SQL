package com.chess.puzzle.text2sql.web.error

/**
 * A sealed class representing possible client-side errors encountered during search operations.
 * Each error specifies the field that caused the issue and a descriptive message.
 */
sealed class ClientError {
    /** The field name that caused the error. */
    abstract val field: String

    /** A human-readable message explaining the error. */
    abstract val message: String

    /** An error indicating that the query field is missing or null. */
    data object MissingQuery : ClientError() {
        override val field: String = "query"
        override val message: String = "Query should not be null"
    }

    /** An error indicating that the specified model is not valid. */
    data object InvalidModel : ClientError() {
        override val field: String = "model"
        override val message: String = "Model is not valid"
    }

    /** An error indicating that the specified model variant is not valid. */
    data object InvalidModelVariant : ClientError() {
        override val field: String = "model"
        override val message: String = "Model Variant is not valid"
    }
}
