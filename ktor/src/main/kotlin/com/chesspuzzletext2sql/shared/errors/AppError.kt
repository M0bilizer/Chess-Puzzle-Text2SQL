package com.chesspuzzletext2sql.shared.errors

import io.ktor.http.HttpStatusCode

interface GenericError

data class StartupError(val message: String) : GenericError

interface ApplicationError : GenericError {
    val status: HttpStatusCode
    val code: String
    val message: String

    val response: Map<String, String>
        get() = mapOf("code" to code, "message" to message)
}

data class UnknownError(override val message: String) : ApplicationError {
    override val status = HttpStatusCode.InternalServerError
    override val code = "UNKNOWN_ERROR"

    override val response: Map<String, String>
        get() =
            mapOf("code" to code, "message" to "Something went wrong...").also { println(message) }

    constructor(throwable: Throwable) : this("${throwable.message}")
}

data class NoTemplateFound(val template: String) : ApplicationError {
    override val status = HttpStatusCode.NotFound
    override val code = "NO_TEMPLATE_FOUND"

    override val message = "No template found for: $template"
}

data class NotFoundError(override val message: String = "Resource cannot be found") :
    ApplicationError {
    override val status = HttpStatusCode.NotFound
    override val code = "NOT_FOUND"
}

data class UnsupportedModel(val model: String) : ApplicationError {
    override val status = HttpStatusCode.NotFound
    override val code = "UNSUPPORTED_MODEL"
    override val message = "Model $model is not supported"
}

data class NoModelConfigFound(val model: String) : ApplicationError {
    override val status = HttpStatusCode.NotFound
    override val code = "NO_MODEL_CONFIG_FOUND"
    override val message = "No model config found for: $model"
}

data object LlmRequestTimeout : ApplicationError {
    override val status = HttpStatusCode.RequestTimeout
    override val code = "LLM_REQUEST_TIMEOUT"
    override val message = "Request to LLM Service timed out"
}

data object LlmTooManyRequests : ApplicationError {
    override val status = HttpStatusCode.TooManyRequests
    override val code = "LLM_TOO_MANY_REQUESTS"
    override val message = "Too many request is sent to LLM API"
}

data object LlmServiceUnavailable : ApplicationError {
    override val status = HttpStatusCode.ServiceUnavailable
    override val code = "LLM_SERVICE_UNAVAILABLE"
    override val message = "The LLM service is unavailable"
}

data object DangerousSqlError : ApplicationError {
    override val status: HttpStatusCode = HttpStatusCode.UnprocessableEntity
    override val code: String = "DANGEROUS_SQL_ERROR"
    override val message: String = "SQL Statement is dangerous"

    override val response: Map<String, String>
        get() = mapOf("code" to code, "message" to "Cannot process search")
}

data object SqlGenerationError : ApplicationError {
    override val status: HttpStatusCode = HttpStatusCode.UnprocessableEntity
    override val code: String = "SQL_GENERATION_FAILED"
    override val message: String = "Failed to generate or execute SQL query"

    override val response: Map<String, String>
        get() = mapOf("code" to code, "message" to "Cannot process search")
}

data object DatabaseConnectionError : ApplicationError {
    override val status: HttpStatusCode = HttpStatusCode.ServiceUnavailable
    override val code: String = "DATABASE_CONNECTION_ERROR"
    override val message: String = "Failed to connect to database"

    override val response: Map<String, String>
        get() = mapOf("code" to code, "message" to "The service is down")
}
