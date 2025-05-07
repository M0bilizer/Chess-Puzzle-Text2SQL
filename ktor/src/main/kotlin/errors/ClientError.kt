package com.chesspuzzletext2sql.errors

import io.ktor.http.HttpStatusCode

sealed class ClientError(val status: HttpStatusCode = HttpStatusCode.BadRequest, message: String) :
    CustomError(message) {
    class InvalidLimit : ClientError(message = "Limit must be positive")

    companion object {}
}

val ClientError.Companion.InvalidLimit
    get() = ClientError.InvalidLimit()
