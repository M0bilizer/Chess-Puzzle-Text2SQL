package com.chesspuzzletext2sql.errors

import io.ktor.http.HttpStatusCode

sealed class ClientError(val status: HttpStatusCode = HttpStatusCode.BadRequest, message: String) :
    CustomError(message) {
    class InvalidLimit : ClientError(message = "Limit must be positive")

    class EmptyMessage : ClientError(message = "Message cannot be empty")

    class UnsupportedModel : ClientError(message = "Unsupported Model")

    companion object {
        val InvalidLimit
            get() = InvalidLimit()

        val EmptyMesssage
            get() = EmptyMessage()

        val UnsupportedModel
            get() = UnsupportedModel()
    }
}
