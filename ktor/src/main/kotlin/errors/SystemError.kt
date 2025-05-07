package com.chesspuzzletext2sql.errors

import io.ktor.http.HttpStatusCode

sealed class SystemError(val status: HttpStatusCode, message: String) : CustomError(message) {
    class CannotConnectToDatabase :
        SystemError(HttpStatusCode.ServiceUnavailable, "Cannot Connect to Database")

    class UnknownError :
        SystemError(HttpStatusCode.InternalServerError, "Something unexpected occurred!")

    companion object {}
}

val SystemError.Companion.CannotConnect
    get() = SystemError.CannotConnectToDatabase()
val SystemError.Companion.UnknownError
    get() = SystemError.UnknownError()
