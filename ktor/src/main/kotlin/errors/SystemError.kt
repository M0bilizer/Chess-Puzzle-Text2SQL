package com.chesspuzzletext2sql.errors

import io.ktor.http.HttpStatusCode

sealed class SystemError(val status: HttpStatusCode, message: String) : CustomError(message) {
    class CannotConnectToDatabase :
        SystemError(HttpStatusCode.ServiceUnavailable, "Cannot Connect to Database")

    class UnknownError :
        SystemError(HttpStatusCode.InternalServerError, "Something unexpected occurred!")

    class MissingPromptTemplate :
        SystemError(HttpStatusCode.ServiceUnavailable, "Missing Prompt Template")

    class FileIOError :
        SystemError(HttpStatusCode.ServiceUnavailable, "IO Error while reading file")

    class EmptyPromptTemplate :
        SystemError(HttpStatusCode.ServiceUnavailable, "Prompt Template is empty!")

    companion object {}
}

val SystemError.Companion.CannotConnect
    get() = SystemError.CannotConnectToDatabase()
val SystemError.Companion.UnknownError
    get() = SystemError.UnknownError()
val SystemError.Companion.MissingPrompt
    get() = SystemError.MissingPromptTemplate()
val SystemError.Companion.FileIO
    get() = SystemError.FileIOError()
val SystemError.Companion.EmptyPrompt
    get() = SystemError.EmptyPromptTemplate()
