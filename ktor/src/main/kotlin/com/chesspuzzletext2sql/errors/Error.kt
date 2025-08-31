package com.chesspuzzletext2sql.errors

import io.ktor.http.HttpStatusCode

sealed class Error : Failure {
  abstract override val type: ErrorType
  abstract val status: HttpStatusCode

  enum class ErrorType(override val code: String, val message: String) : FailureType {
    CustomError("custom_error", "Custom Error"),
    CannotConnectToDatabase("cannot_connect_to_database", "Cannot connect to database"),
    SQLException("sql_exception", "Exception when executing SQL statement"),
    IOException("io_exception", "Cannot connect to LLM"),
    LLMServerError("llm_server_error", "LLM server error"),
    LLMServiceUnavailable("llm_service_unavailable", "LLM service unavailable"),
    TimeoutException("timeout_exception", "Could not complete request within expected timeframe"),
    TooManyRequests("too_many_requests", "Too many requests sent"),
    PaymentRequired("payment_required", "Contact system administrator"),
    UnknownError("unknown_error", "Something unexpected occurred"),
  }

  data object CustomError : Error() {
    override val type: ErrorType = ErrorType.CustomError
    override val status: HttpStatusCode = HttpStatusCode.InternalServerError
  }

  data object CannotConnectToDatabase : Error() {
    override val type: ErrorType = ErrorType.CannotConnectToDatabase
    override val status: HttpStatusCode = HttpStatusCode.ServiceUnavailable
  }

  data object SQLException : Error() {
    override val type: ErrorType = ErrorType.SQLException
    override val status: HttpStatusCode = HttpStatusCode.InternalServerError
  }

  data object IOException : Error() {
    override val type: ErrorType = ErrorType.IOException
    override val status: HttpStatusCode = HttpStatusCode.InternalServerError
  }

  data object LLMServerError : Error() {
    override val type: ErrorType = ErrorType.LLMServerError
    override val status: HttpStatusCode = HttpStatusCode.InternalServerError
  }

  data object LLMServiceUnavailable : Error() {
    override val type: ErrorType = ErrorType.LLMServiceUnavailable
    override val status: HttpStatusCode = HttpStatusCode.ServiceUnavailable
  }

  data object TimeoutException : Error() {
    override val type: ErrorType = ErrorType.TimeoutException
    override val status: HttpStatusCode = HttpStatusCode.InternalServerError
  }

  data object TooManyRequests : Error() {
    override val type: ErrorType = ErrorType.TooManyRequests
    override val status: HttpStatusCode = HttpStatusCode.TooManyRequests
  }

  data object PaymentRequired : Error() {
    override val type: ErrorType = ErrorType.PaymentRequired
    override val status: HttpStatusCode = HttpStatusCode.PaymentRequired
  }

  data object UnknownError : Error() {
    override val type: ErrorType = ErrorType.UnknownError
    override val status: HttpStatusCode = HttpStatusCode.InternalServerError
  }
}
