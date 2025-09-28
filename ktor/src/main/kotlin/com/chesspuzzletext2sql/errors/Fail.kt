package com.chesspuzzletext2sql.errors

sealed class Fail : Failure {
    abstract override val type: FailType
    abstract val details: List<FailDetail>

    enum class FailType(override val code: String, val message: String) : FailureType {
        MalformedJson("malformed_json", "Malformed JSON request"),
        InvalidRequest("invalid_request", "Request body is invalid"),
        InvalidParameter("invalid_value", "One or more provided value is invalid"),
    }

    data object MalformedJson : Fail() {
        override val type: FailType = FailType.MalformedJson
        override val details: List<FailDetail> = listOf(InvalidJsonDetail())
    }

    data class InvalidRequest(override val details: List<InvalidRequestDetail>) : Fail() {
        override val type: FailType = FailType.InvalidRequest
    }

    data class InvalidParameter(override val details: List<InvalidParameterDetail>) : Fail() {
        override val type: FailType = FailType.InvalidParameter
    }
}

interface FailDetail {
    val field: String
    val code: String
    val description: FailMessage
}

sealed interface FailMessage {
    val code: String
    val description: String
}

/** ** */
class InvalidJsonDetail() : FailDetail {
    override val description: FailMessage = InvalidJsonErrorMessage
    override val code: String = description.code
    override val field: String = "request_body"
}

object InvalidJsonErrorMessage : FailMessage {
    override val code: String = "invalid_json"
    override val description: String = "The request body contains invalid JSON syntax"
}

/** ** */
class InvalidRequestDetail(
    override val field: String,
    override val description: InvalidRequestMessage,
) : FailDetail {
    override val code: String
        get() = description.code
}

sealed class InvalidRequestMessage(override val code: String, override val description: String) :
    FailMessage {

    class MissingField(val field: String) :
        InvalidRequestMessage("missing_field", "Field '$field' is required but was not provided")

    class UnexpectedField(val field: String) :
        InvalidRequestMessage(
            "unexpected_field",
            "Field '$field' is not allowed for this resource",
        )

    class TypeMismatch(val field: String, val receivedValue: Any?, val expectedType: String) :
        InvalidRequestMessage(
            "type_mismatch",
            "Field '$field' is expecting $expectedType but received ${formatValue(receivedValue)}",
        )

    companion object {
        private fun formatValue(value: Any?): String {
            return when (value) {
                null -> "null"
                is Boolean -> "a boolean"
                is String -> "a string"
                is Float -> "a float"
                is Number -> "a number"
                is Array<*> -> "an array"
                else -> value.toString()
            }
        }
    }
}

/** ** */
class InvalidParameterDetail(
    override val field: String,
    override val description: InvalidParameterMessage,
) : FailDetail {
    override val code: String
        get() = description.code
}

sealed class InvalidParameterMessage(override val code: String, override val description: String) :
    FailMessage {

    sealed class GenericConstraint(override val code: String, override val description: String) :
        InvalidParameterMessage(code, description) {
        // String constraints
        data class IsEmpty(val field: String) :
            GenericConstraint("must_be_empty", "Field '$field' must be empty")

        data class IsNotEmpty(val field: String) :
            GenericConstraint("must_not_be_empty", "Field '$field' must not be empty")

        // Number constraints
        data class IsPositive(val field: String) :
            GenericConstraint("must_be_positive", "Field '$field' must be positive")

        data class IsNegative(val field: String) :
            GenericConstraint("must_be_negative", "Field '$field' must be negative")
    }

    sealed class CustomConstraint(code: String, description: String) :
        InvalidParameterMessage(code, description) {

        data object UnsupportedTemplate :
            CustomConstraint("unsupported_template", "Template is not supported")

        data object InvalidSql : CustomConstraint("invalid_sql", "Sql is not valid")

        data object UnallowedSql : CustomConstraint("unallowed_sql", "Sql is not allowed")

        data object UnavailableModel :
            CustomConstraint("unavailable_model", "Model is not available")

        data object UnsupportedModel :
            CustomConstraint("unsupported_model", "Model is not supported")

        companion object {
            val entries: Map<String, CustomConstraint> by lazy {
                mapOf(
                    "unsupported_template" to UnsupportedTemplate,
                    "invalid_sql" to InvalidSql,
                    "unallowed_sql" to UnallowedSql,
                    "unavailable_model" to UnavailableModel,
                    "unsupported_model" to UnsupportedModel,
                )
            }

            fun fromCode(code: String): CustomConstraint? = entries[code]
        }
    }

    data object UnknownConstraint :
        InvalidParameterMessage("unknown_constraint", "Unknown constraint") {
        init {
            error(
                "Unknown constraint encountered. This indicates a missing constraint registration."
            )
        }
    }
}
