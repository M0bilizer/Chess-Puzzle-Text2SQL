package com.chesspuzzletext2sql.errors

import io.ktor.http.HttpStatusCode

sealed class ClientError(val status: HttpStatusCode = HttpStatusCode.BadRequest, message: String) :
    CustomError(message) {
    class InvalidCount : ClientError(message = "Count must be positive")

    class EmptyMessage : ClientError(message = "Message cannot be empty")

    class UnsupportedModel : ClientError(message = "Unsupported Model")

    companion object {
        val InvalidCount
            get() = InvalidCount()

        val EmptyMesssage
            get() = EmptyMessage()

        val UnsupportedModel
            get() = UnsupportedModel()
    }
}
