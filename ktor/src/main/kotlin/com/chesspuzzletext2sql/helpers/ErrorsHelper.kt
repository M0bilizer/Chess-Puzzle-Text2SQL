package com.chesspuzzletext2sql.helpers

import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.InvalidParameterDetail
import com.chesspuzzletext2sql.errors.InvalidParameterMessage
import com.chesspuzzletext2sql.errors.InvalidRequestDetail
import com.chesspuzzletext2sql.errors.InvalidRequestMessage
import dev.nesk.akkurate.constraints.ConstraintViolationSet
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull

fun mapViolationsToErrors(violationSet: ConstraintViolationSet): Fail.InvalidParameter {
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

object GenericConstraintRegistry {
    private val constraints =
        mutableMapOf<String, (String) -> InvalidParameterMessage.GenericConstraint>()

    init {
        registerStringConstraints()
        registerNumberConstraints()
    }

    // String constraints
    private fun registerStringConstraints() {
        register("Must be empty") { field ->
            InvalidParameterMessage.GenericConstraint.IsEmpty(field)
        }
        register("Must not be empty") { field ->
            InvalidParameterMessage.GenericConstraint.IsNotEmpty(field)
        }
    }

    // Number constraints
    private fun registerNumberConstraints() {
        register("Must be positive") { field ->
            InvalidParameterMessage.GenericConstraint.IsPositive(field)
        }
        register("Must be negative") { field ->
            InvalidParameterMessage.GenericConstraint.IsNegative(field)
        }
    }

    private fun register(
        message: String,
        creator: (String) -> InvalidParameterMessage.GenericConstraint,
    ) {
        constraints[message] = creator
    }

    fun parseConstraint(
        constraintMessage: String,
        field: String,
    ): InvalidParameterMessage.GenericConstraint? {
        val creator = constraints[constraintMessage] ?: return null
        return creator(field)
    }
}

fun validateJsonStructure(jsonElement: JsonElement, targetClass: KClass<*>): Fail? {
    if (jsonElement !is JsonObject) {
        return Fail.MalformedJson
    }

    val details = mutableListOf<InvalidRequestDetail>()

    val expectedProperties = getExpectedProperties(targetClass)
    val expectedPropertiesName = expectedProperties.map { it.name }.toSet()

    // Check for extra properties
    jsonElement.keys.forEach { key ->
        if (key !in expectedPropertiesName) {
            details.add(InvalidRequestDetail(key, InvalidRequestMessage.UnexpectedField(key)))
        }
    }

    // Check required properties and types
    expectedProperties.forEach { prop ->
        val jsonValue = jsonElement[prop.name]

        // Check if required field is missing
        if (jsonValue == null) {
            if (prop.isRequired) {
                details.add(
                    InvalidRequestDetail(prop.name, InvalidRequestMessage.MissingField(prop.name))
                )
            }
            return@forEach
        }

        // Check type compatibility (basic implementation)
        if (!isTypeCompatible(jsonValue, prop.returnType)) {
            val receivedType = getJsonValueType(jsonValue)
            details.add(
                InvalidRequestDetail(
                    prop.name,
                    InvalidRequestMessage.TypeMismatch(
                        prop.name,
                        receivedType,
                        prop.returnType.toString(),
                    ),
                )
            )
        }
    }

    return details.takeUnless { it.isEmpty() }?.let { Fail.InvalidRequest(it) }
}

private fun isTypeCompatible(jsonValue: JsonElement, expectedType: KType): Boolean {
    return when (expectedType.classifier) {
        String::class -> jsonValue is JsonPrimitive && jsonValue.isString
        Int::class -> jsonValue is JsonPrimitive && jsonValue.intOrNull != null
        Boolean::class -> jsonValue is JsonPrimitive && jsonValue.booleanOrNull != null
        Double::class -> jsonValue is JsonPrimitive && jsonValue.doubleOrNull != null
        List::class -> false
        // Add more type checks as needed
        else -> true // For complex types, defer to deserialization
    }
}

private fun getExpectedProperties(targetClass: KClass<*>): List<PropertyInfo> {
    return targetClass.memberProperties.map { prop ->
        PropertyInfo(
            name = prop.name,
            isRequired = prop.returnType.isMarkedNullable.not(),
            returnType = prop.returnType,
        )
    }
}

private fun getJsonValueType(jsonValue: JsonElement): String {
    return when (jsonValue) {
        is JsonNull -> "null"
        is JsonPrimitive -> {
            when {
                jsonValue.isString -> "string"
                jsonValue.intOrNull != null -> "number (int)"
                jsonValue.doubleOrNull != null -> "number (double)"
                jsonValue.booleanOrNull != null -> "boolean"
                else -> "unknown primitive"
            }
        }

        is JsonArray -> "array"
        is JsonObject -> "object"
    }
}

private data class PropertyInfo(val name: String, val isRequired: Boolean, val returnType: KType)
