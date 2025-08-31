package com.chesspuzzletext2sql.errors

sealed class Fail : Failure {
  abstract override val type: FailType
  abstract val details: List<FailDetail>

  enum class FailType(override val code: String, val message: String) : FailureType {
    MissingFields("missing_fields", "Required fields are missing"),
    ValidationFailed("validation_failed", "Validation failed"),
  }

  data class MissingFields(override val details: List<MissingFieldDetail>) : Fail() {
    override val type: FailType = FailType.MissingFields
  }

  data class ValidationFailed(override val details: List<ValidationFailedDetail>) : Fail() {
    override val type: FailType = FailType.ValidationFailed
  }
}

interface FailDetail {
  val field: String
  val code: String
  val message: FailMessage
}

class MissingFieldDetail
private constructor(override val field: String, override val message: MissingFieldMessage) :
  FailDetail {
  constructor(field: String) : this(field, MissingFieldMessage(field))

  override val code: String
    get() = message.code
}

class ValidationFailedDetail(
  override val field: String,
  override val message: ValidationErrorMessage,
) : FailDetail {
  override val code: String
    get() = message.code
}

sealed interface FailMessage {
  val code: String
  val description: String
}

class MissingFieldMessage(field: String) : FailMessage {
  override val code: String = "missing_field"
  override val description: String = "$field is required"
}

enum class ValidationErrorMessage(override val code: String, override val description: String) :
  FailMessage {
  InvalidCount("invalid_count", "Count must be positive"),
  EmptyMessage("empty_message", "Message cannot be empty"),
  UnsupportedModel("unsupported_model", "Unsupported model"),
  UnavailableModel("unavailable_model", "Model is unavailable"),
  EmptyQuery("empty_query", "Query cannot be empty"),
  InvalidQuery("invalid_query", "Query is invalid"),
  UnallowedQuery("unallowed_query", "Query is not allowed"),
  EmptyTemplate("empty_template", "Template cannot be empty"),
  UnsupportedTemplate("unsupported_template", "Unsupported template"),
}
