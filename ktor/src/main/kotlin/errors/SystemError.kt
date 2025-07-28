package com.chesspuzzletext2sql.errors

import io.ktor.http.HttpStatusCode

sealed class SystemError(val status: HttpStatusCode, message: String) : CustomError(message) {
    class CannotConnectToDatabase :
        SystemError(HttpStatusCode.ServiceUnavailable, "Cannot Connect to Database")

    class UnknownError :
        SystemError(HttpStatusCode.InternalServerError, "Something unexpected occurred!")

    class UnavailableModel : SystemError(HttpStatusCode.ServiceUnavailable, "Model is unavailable")

    class IOException : SystemError(HttpStatusCode.InternalServerError, "Cannot Connect to LLM")

    class TimeoutException :
        SystemError(
            HttpStatusCode.InternalServerError,
            "Could not complete request within expected timeframe",
        )

    class PaymentRequired :
        SystemError(HttpStatusCode.InternalServerError, "Contact System Administrator")

    class TooManyRequests :
        SystemError(HttpStatusCode.InternalServerError, "Too many request sent")

    class LLMServerError : SystemError(HttpStatusCode.InternalServerError, "LLM Server Error")

    class LLMServiceUnavailable :
        SystemError(HttpStatusCode.InternalServerError, "LLM Service Unavailable")

    class SQLException() :
        SystemError(
            HttpStatusCode.InternalServerError,
            message = "Exception when executing SQL statement",
        )

    companion object {
        val CannotConnect
            get() = CannotConnectToDatabase()

        val UnknownError
            get() = UnknownError()

        val UnavailableModel
            get() = UnavailableModel()

        val IOException
            get() = IOException()

        val TimeoutException
            get() = TimeoutException()

        val PaymentRequired
            get() = PaymentRequired()

        val TooManyRequests
            get() = TooManyRequests()

        val LLMServerError
            get() = LLMServerError()

        val LLMServiceUnavailable
            get() = LLMServiceUnavailable()

        val SQLException
            get() = SQLException()
    }
}
