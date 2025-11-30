// validators/JsonParsing.kt
package com.chesspuzzletext2sql.shared.validator

import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.InvalidRequestDetail
import com.chesspuzzletext2sql.errors.InvalidRequestMessage
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull

inline fun <reified A : Any> parseJson(
    jsonElement: JsonElement,
    json: Json = Json.Default,
): Result<A, Fail> =
    runCatching {
            // First validate the structure
            validateJsonStructure(jsonElement, A::class)?.let { error ->
                return Err(error)
            }

            // Then parse using kotlinx.serialization
            json.decodeFromJsonElement<A>(jsonElement)
        }
        .mapError { error ->
            when (error) {
                is SerializationException -> {
                    // Extract field-level errors from SerializationException
                    val fieldErrors = extractFieldErrors(error)
                    if (fieldErrors.isNotEmpty()) {
                        Fail.InvalidRequest(fieldErrors)
                    } else {
                        Fail.MalformedJson
                    }
                }

                else ->
                    Fail.InvalidRequest(
                        listOf(
                            InvalidRequestDetail(
                                "root",
                                InvalidRequestMessage.TypeMismatch(
                                    "root",
                                    "Failed to parse JSON: ${error.message}",
                                    "Valid ${A::class.simpleName} structure",
                                ),
                            )
                        )
                    )
            }
        }

// Pure function - returns validation result or null if valid
fun <A : Any> validateJsonStructure(jsonElement: JsonElement, targetClass: KClass<A>): Fail? {
    if (jsonElement !is JsonObject) {
        return Fail.MalformedJson
    }

    val expectedProperties = getExpectedProperties(targetClass)
    val expectedPropertiesName = expectedProperties.map { it.name }.toSet()

    val details = buildList {
        // Check for extra properties
        jsonElement.keys.forEach { key ->
            if (key !in expectedPropertiesName) {
                add(InvalidRequestDetail(key, InvalidRequestMessage.UnexpectedField(key)))
            }
        }

        // Check required properties and types
        expectedProperties.forEach { prop ->
            val jsonValue = jsonElement[prop.name]
            validateProperty(prop, jsonValue)?.let { detail -> add(detail) }
        }
    }

    return details.takeIf { it.isNotEmpty() }?.let { Fail.InvalidRequest(it) }
}

// Keep all your existing helper functions...
private fun validateProperty(
    property: PropertyInfo,
    jsonValue: JsonElement?,
): InvalidRequestDetail? {
    if (jsonValue == null) {
        return if (property.isRequired) {
            InvalidRequestDetail(property.name, InvalidRequestMessage.MissingField(property.name))
        } else {
            null
        }
    }

    if (!isTypeCompatible(jsonValue, property.returnType)) {
        val receivedType = getJsonValueType(jsonValue)
        return InvalidRequestDetail(
            property.name,
            InvalidRequestMessage.TypeMismatch(
                property.name,
                receivedType,
                property.returnType.toString(),
            ),
        )
    }

    return null
}

private fun isTypeCompatible(jsonValue: JsonElement, expectedType: KType): Boolean {
    return when (expectedType.classifier) {
        String::class -> jsonValue is JsonPrimitive && jsonValue.isString
        Int::class -> jsonValue is JsonPrimitive && jsonValue.intOrNull != null
        Boolean::class -> jsonValue is JsonPrimitive && jsonValue.booleanOrNull != null
        Double::class -> jsonValue is JsonPrimitive && jsonValue.doubleOrNull != null
        List::class -> jsonValue is JsonArray
        else -> true
    }
}

private fun getJsonValueType(jsonValue: JsonElement): String =
    when (jsonValue) {
        is JsonNull -> "null"
        is JsonPrimitive ->
            when {
                jsonValue.isString -> "string"
                jsonValue.intOrNull != null -> "number (int)"
                jsonValue.doubleOrNull != null -> "number (double)"
                jsonValue.booleanOrNull != null -> "boolean"
                else -> "unknown primitive"
            }

        is JsonArray -> "array"
        is JsonObject -> "object"
    }

private fun <A : Any> getExpectedProperties(targetClass: KClass<A>): List<PropertyInfo> =
    targetClass.memberProperties.map { prop ->
        PropertyInfo(
            name = prop.name,
            isRequired = !prop.returnType.isMarkedNullable,
            returnType = prop.returnType,
        )
    }

private data class PropertyInfo(val name: String, val isRequired: Boolean, val returnType: KType)

fun extractFieldErrors(error: SerializationException): List<InvalidRequestDetail> {
    val message = error.message ?: return emptyList()

    return buildList {
        val requiredFieldPattern = """Field '([^']+)' is required""".toRegex()
        requiredFieldPattern.findAll(message).forEach { match ->
            val fieldName = match.groupValues[1]
            add(InvalidRequestDetail(fieldName, InvalidRequestMessage.MissingField(fieldName)))
        }

        val typeMismatchPattern = """for field '([^']+)'""".toRegex()
        typeMismatchPattern.find(message)?.let { match ->
            val fieldName = match.groupValues[1]
            val expectedActualPattern = """Expected ([^,]+), but was (\S+)""".toRegex()
            val typeMatch = expectedActualPattern.find(message)
            val expectedType = typeMatch?.groupValues?.get(1) ?: "expected type"
            val actualType = typeMatch?.groupValues?.get(2) ?: "actual type"

            add(
                InvalidRequestDetail(
                    fieldName,
                    InvalidRequestMessage.TypeMismatch(fieldName, actualType, expectedType),
                )
            )
        }

        if (isEmpty() && message.contains("but was", ignoreCase = true)) {
            add(
                InvalidRequestDetail(
                    "root",
                    InvalidRequestMessage.TypeMismatch(
                        "root",
                        "invalid type",
                        "valid JSON structure",
                    ),
                )
            )
        }
    }
}
