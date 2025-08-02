package com.chesspuzzletext2sql.errors

import io.ktor.http.HttpStatusCode

sealed class SystemError(val status: HttpStatusCode, override val message: String) : CustomError() {
    object CannotConnectToDatabase :
        SystemError(HttpStatusCode.ServiceUnavailable, "Cannot connect to database")

    object SQLException :
        SystemError(HttpStatusCode.InternalServerError, "Exception when executing SQL statement")

    object IOException : SystemError(HttpStatusCode.InternalServerError, "Cannot connect to LLM")

    object LLMServerError : SystemError(HttpStatusCode.InternalServerError, "LLM server error")

    object LLMServiceUnavailable :
        SystemError(HttpStatusCode.ServiceUnavailable, "LLM service unavailable")

    object TimeoutException :
        SystemError(
            HttpStatusCode.InternalServerError,
            "Could not complete request within expected timeframe",
        )

    object TooManyRequests : SystemError(HttpStatusCode.TooManyRequests, "Too many requests sent")

    object PaymentRequired :
        SystemError(HttpStatusCode.PaymentRequired, "Contact system administrator")

    object UnknownError :
        SystemError(HttpStatusCode.InternalServerError, "Something unexpected occurred")
}
