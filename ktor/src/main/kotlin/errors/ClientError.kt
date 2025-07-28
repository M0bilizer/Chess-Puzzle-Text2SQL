package com.chesspuzzletext2sql.errors

import io.ktor.http.HttpStatusCode

sealed class ClientError(val status: HttpStatusCode = HttpStatusCode.BadRequest, message: String) :
    CustomError(message) {
    class InvalidCount : ClientError(message = "Count must be positive")

    class EmptyMessage : ClientError(message = "Message cannot be empty")

    class UnsupportedModel : ClientError(message = "Unsupported Model")

    class EmptyQuery : ClientError(message = "Query cannot be empty")

    class InvalidQuery : ClientError(message = "Query is invalid")

    class UnallowedQuery : ClientError(message = "Query is unallowed")

    companion object {
        val InvalidCount
            get() = InvalidCount()

        val EmptyMessage
            get() = EmptyMessage()

        val UnsupportedModel
            get() = UnsupportedModel()

        val EmptyQuery
            get() = EmptyQuery()

        val InvalidQuery
            get() = InvalidQuery()

        val UnallowedQuery
            get() = UnallowedQuery()
    }
}
