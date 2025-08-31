package com.chesspuzzletext2sql.helpers

import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.MissingFieldDetail
import com.chesspuzzletext2sql.errors.ValidationErrorMessage
import com.chesspuzzletext2sql.errors.ValidationFailedDetail
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.ktor.server.request.ApplicationRequest
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class MissingFieldValidator(private val request: ApplicationRequest) {
  private val errors = mutableListOf<MissingFieldDetail>()

  fun mustNotBeNull(field: String) {
    if (request.queryParameters[field].isNullOrBlank()) {
      errors.add(MissingFieldDetail(field))
    }
  }

  fun validate(): com.github.michaelbull.result.Result<Unit, Fail.MissingFields> {
    return if (errors.isEmpty()) Ok(Unit) else Err(Fail.MissingFields(errors))
  }
}

class FieldValidator(private val request: ApplicationRequest) {
  private val errors = mutableListOf<ValidationFailedDetail>()

  inner class ValidationBuilder(val field: String, val predicate: (String) -> Boolean) {
    infix fun withMessage(message: ValidationErrorMessage) {
      request.queryParameters[field]?.let { value ->
        if (!predicate(value)) {
          errors.add(ValidationFailedDetail(field, message))
        }
      }
    }
  }

  fun must(field: String, predicate: (String) -> Boolean): ValidationBuilder {
    return ValidationBuilder(field, predicate)
  }

  fun validate(): com.github.michaelbull.result.Result<Unit, Fail.ValidationFailed> {
    return if (errors.isEmpty()) Ok(Unit) else Err(Fail.ValidationFailed(errors))
  }
}

fun validateMissing(
  request: ApplicationRequest,
  block: MissingFieldValidator.() -> Unit,
): com.github.michaelbull.result.Result<Unit, Fail.MissingFields> {
  val validator = MissingFieldValidator(request)
  validator.block()
  return validator.validate()
}

fun validate(
  request: ApplicationRequest,
  block: FieldValidator.() -> Unit,
): com.github.michaelbull.result.Result<Unit, Fail.ValidationFailed> {
  val validator = FieldValidator(request)
  validator.block()
  return validator.validate()
}

class JsonFieldValidator(private val data: Any) {
  private val errors = mutableListOf<ValidationFailedDetail>()

  inner class ValidationBuilder(val field: String, val predicate: (Any?) -> Boolean) {
    infix fun withMessage(message: ValidationErrorMessage) {
      val value = getFieldValue(field)
      if (!predicate(value)) {
        errors.add(ValidationFailedDetail(field, message))
      }
    }
  }

  private fun getFieldValue(field: String): Any? {
    return when (data) {
      is Map<*, *> -> data[field]
      else -> {
        val property = data::class.memberProperties.firstOrNull { it.name == field }
        property?.let { @Suppress("UNCHECKED_CAST") ((it as KProperty1<Any, *>).get(data)) }
      }
    }
  }

  fun must(field: String, predicate: (Any?) -> Boolean): ValidationBuilder {
    return ValidationBuilder(field, predicate)
  }

  fun validate(): com.github.michaelbull.result.Result<Unit, Fail.ValidationFailed> {
    return if (errors.isEmpty()) Ok(Unit) else Err(Fail.ValidationFailed(errors))
  }
}

fun validateJson(
  data: Any,
  block: JsonFieldValidator.() -> Unit,
): Result<Unit, Fail.ValidationFailed> {
  val validator = JsonFieldValidator(data)
  validator.block()
  return validator.validate()
}
