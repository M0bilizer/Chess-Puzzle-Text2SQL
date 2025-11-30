package com.chesspuzzletext2sql.shared.validator

import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.InvalidParameterDetail
import com.chesspuzzletext2sql.errors.InvalidParameterMessage
import com.chesspuzzletext2sql.helpers.GenericConstraintRegistry
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.nesk.akkurate.ValidationResult
import dev.nesk.akkurate.Validator
import dev.nesk.akkurate.constraints.ConstraintViolationSet

fun <A> validateBusinessLogic(request: A, validator: Validator.Runner<A>): Result<A, Fail> {
    return when (val result = validator(request)) {
        is ValidationResult.Success -> Ok(result.value)
        is ValidationResult.Failure -> Err(mapViolationsToErrors(result.violations))
    }
}

private fun mapViolationsToErrors(violationSet: ConstraintViolationSet): Fail.InvalidParameter {
    val details =
        violationSet.map { (constraintMessage, path) ->
            val fieldName = extractFieldName(path)

            try {
                val message =
                    InvalidParameterMessage.CustomConstraint.fromCode(constraintMessage)
                        ?: GenericConstraintRegistry.parseConstraint(constraintMessage, fieldName)
                        ?: InvalidParameterMessage.UnknownConstraint
                InvalidParameterDetail(fieldName, message)
            } catch (e: ExceptionInInitializerError) {
                // This should never happen if all constraints are registered
                val errorMessage =
                    "Missing constraint registration: $constraintMessage. Field: $fieldName"
                throw IllegalStateException(errorMessage, e)
            }
        }

    return Fail.InvalidParameter(details)
}

private fun extractFieldName(path: List<Any>): String {
    return path.joinToString(".") { it.toString() }
}
