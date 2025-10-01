package com.chesspuzzletext2sql.helpers

import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.InvalidParameterDetail
import com.chesspuzzletext2sql.errors.InvalidParameterMessage
import com.chesspuzzletext2sql.errors.InvalidRequestDetail
import com.chesspuzzletext2sql.errors.InvalidRequestMessage
import dev.nesk.akkurate.constraints.ConstraintViolation
import dev.nesk.akkurate.constraints.ConstraintViolationSet
import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import strikt.api.expectThat
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNull

class ValidationHelpersTest :
    FunSpec({

        // Test data classes for JSON structure validation
        data class SimpleClass(val name: String, val age: Int)
        data class OptionalClass(val required: String, val optional: String? = null)
        data class MixedTypesClass(
            val stringField: String,
            val intField: Int,
            val booleanField: Boolean,
        )

        context("mapViolationsToErrors") {
            test("should map constraint violations to InvalidParameter errors") {
                // Given
                val violations =
                    ConstraintViolationSet(
                        setOf(
                            ConstraintViolation("Must not be empty", listOf("user", "name")),
                            ConstraintViolation("Must be positive", listOf("user", "age")),
                        )
                    )

                // When
                val result = mapViolationsToErrors(violations)

                // Then
                expectThat(result) {
                    isA<Fail.InvalidParameter>()
                    get { details }.hasSize(2)
                }

                val expectedDetails =
                    listOf(
                        InvalidParameterDetail(
                            "user.name",
                            InvalidParameterMessage.GenericConstraint.IsNotEmpty("user.name"),
                        ),
                        InvalidParameterDetail(
                            "user.age",
                            InvalidParameterMessage.GenericConstraint.IsPositive("user.age"),
                        ),
                    )

                expectThat(result.details).containsExactlyInAnyOrder(expectedDetails)
            }

            test("should handle nested field paths correctly") {
                // Given
                val violations =
                    ConstraintViolationSet(
                        setOf(
                            ConstraintViolation(
                                "Must not be empty",
                                listOf("users", "0", "profile", "firstName"),
                            )
                        )
                    )

                // When
                val result = mapViolationsToErrors(violations)

                // Then
                expectThat(result.details.first()).isA<InvalidParameterDetail>().and {
                    get { field }.isEqualTo("users.0.profile.firstName")
                    get { description }
                        .isEqualTo(
                            InvalidParameterMessage.GenericConstraint.IsNotEmpty(
                                "users.0.profile.firstName"
                            )
                        )
                }
            }

            test("should throw IllegalStateException for unregistered constraints") {
                // Given
                val violations =
                    ConstraintViolationSet(
                        setOf(ConstraintViolation("Unknown constraint", listOf("field")))
                    )

                // When/Then
                val exception =
                    kotlin.runCatching { mapViolationsToErrors(violations) }.exceptionOrNull()

                expectThat(exception)
                    .isA<IllegalStateException>()
                    .get { message }
                    .isEqualTo("Missing constraint registration: Unknown constraint. Field: field")
            }
        }

        context("GenericConstraintRegistry") {
            test("should parse registered string constraints") {
                // When
                val result =
                    GenericConstraintRegistry.parseConstraint("Must not be empty", "username")

                // Then
                expectThat(result)
                    .isEqualTo(InvalidParameterMessage.GenericConstraint.IsNotEmpty("username"))
            }

            test("should parse registered number constraints") {
                // When
                val result = GenericConstraintRegistry.parseConstraint("Must be positive", "age")

                // Then
                expectThat(result)
                    .isEqualTo(InvalidParameterMessage.GenericConstraint.IsPositive("age"))
            }

            test("should return null for unregistered constraints") {
                // When
                val result =
                    GenericConstraintRegistry.parseConstraint("Unregistered constraint", "field")

                // Then
                expectThat(result).isNull()
            }
        }

        context("validateJsonStructure") {
            test("should return MalformedJson for non-object JSON") {
                // Given
                val jsonArray = JsonArray(listOf(JsonPrimitive("test")))
                val jsonPrimitive = JsonPrimitive("string")
                val jsonNull = JsonNull

                // When/Then
                expectThat(validateJsonStructure(jsonArray, SimpleClass::class))
                    .isEqualTo(Fail.MalformedJson)

                expectThat(validateJsonStructure(jsonPrimitive, SimpleClass::class))
                    .isEqualTo(Fail.MalformedJson)

                expectThat(validateJsonStructure(jsonNull, SimpleClass::class))
                    .isEqualTo(Fail.MalformedJson)
            }

            test("should return null for valid JSON matching target class") {
                // Given
                val validJson = buildJsonObject {
                    put("name", "John")
                    put("age", 25)
                }

                // When
                val result = validateJsonStructure(validJson, SimpleClass::class)

                // Then
                expectThat(result).isNull()
            }

            test("should detect extra fields") {
                // Given
                val jsonWithExtra = buildJsonObject {
                    put("name", "John")
                    put("age", 25)
                    put("extraField", "should not be here")
                }

                // When
                val result = validateJsonStructure(jsonWithExtra, SimpleClass::class)

                // Then
                expectThat(result).isNotNull().and {
                    isA<Fail.InvalidRequest>()
                    get { details }.hasSize(1)
                    get { details.first() }
                        .isEqualTo(
                            InvalidRequestDetail(
                                "extraField",
                                InvalidRequestMessage.UnexpectedField("extraField"),
                            )
                        )
                }
            }

            test("should detect missing required fields") {
                // Given
                val jsonMissingRequired = buildJsonObject {
                    put("name", "John")
                    // Missing "age" field
                }

                // When
                val result = validateJsonStructure(jsonMissingRequired, SimpleClass::class)

                // Then
                expectThat(result).isNotNull().and {
                    isA<Fail.InvalidRequest>()
                    get { details }.hasSize(1)
                    get { details.first() }
                        .isEqualTo(
                            InvalidRequestDetail("age", InvalidRequestMessage.MissingField("age"))
                        )
                }
            }

            test("should not require optional fields") {
                // Given
                val jsonWithoutOptional = buildJsonObject {
                    put("required", "value")
                    // optional field is missing, but that's OK
                }

                // When
                val result = validateJsonStructure(jsonWithoutOptional, OptionalClass::class)

                // Then
                expectThat(result).isNull()
            }

            test("should validate type compatibility") {
                // Given
                val jsonWithWrongType = buildJsonObject {
                    put("name", 123) // Should be string
                    put("age", 25)
                }

                // When
                val result = validateJsonStructure(jsonWithWrongType, SimpleClass::class)

                // Then
                expectThat(result).isNotNull().and {
                    isA<Fail.InvalidRequest>()
                    get { details }.hasSize(1)
                    get { details.first() }
                        .isEqualTo(
                            InvalidRequestDetail(
                                "name",
                                InvalidRequestMessage.TypeMismatch(
                                    "name",
                                    "number (int)",
                                    "kotlin.String",
                                ),
                            )
                        )
                }
            }

            test("should handle multiple validation errors") {
                // Given
                val invalidJson = buildJsonObject {
                    put("extraField", "unexpected")
                    put("name", 123) // wrong type
                    // missing required "age" field
                }

                // When
                val result = validateJsonStructure(invalidJson, SimpleClass::class)

                // Then
                expectThat(result).isNotNull().and {
                    isA<Fail.InvalidRequest>()
                    get { details }.hasSize(3)
                }

                val expectedDetails =
                    listOf(
                        InvalidRequestDetail(
                            "extraField",
                            InvalidRequestMessage.UnexpectedField("extraField"),
                        ),
                        InvalidRequestDetail(
                            "name",
                            InvalidRequestMessage.TypeMismatch(
                                "name",
                                "number (int)",
                                "kotlin.String",
                            ),
                        ),
                        InvalidRequestDetail("age", InvalidRequestMessage.MissingField("age")),
                    )

                expectThat(result).isNotNull().and {
                    get { details } containsExactlyInAnyOrder (expectedDetails)
                }
            }

            test("should validate various primitive types") {
                // Given
                val validJson = buildJsonObject {
                    put("stringField", "hello")
                    put("intField", 42)
                    put("booleanField", true)
                }

                // When
                val result = validateJsonStructure(validJson, MixedTypesClass::class)

                // Then
                expectThat(result).isNull()
            }

            test("should detect various type mismatches") {
                // Given
                val invalidJson = buildJsonObject {
                    put("stringField", 123) // should be string
                    put("intField", "not_a_number") // should be int
                    put("booleanField", "not_a_boolean") // should be boolean
                }

                // When
                val result = validateJsonStructure(invalidJson, MixedTypesClass::class)

                // Then
                expectThat(result).isNotNull().and {
                    isA<Fail.InvalidRequest>()
                    get { details }.hasSize(3)
                }
            }
        }

        context("JSON parsing integration") {
            test("should work with parsed JSON from string") {
                // Given
                val jsonString = """{"name": "John", "age": 25}"""
                val jsonElement = Json.parseToJsonElement(jsonString)

                // When
                val result = validateJsonStructure(jsonElement, SimpleClass::class)

                // Then
                expectThat(result).isNull()
            }

            test("should detect errors in parsed JSON") {
                // Given
                val jsonString = """{"name": "John", "age": "not_a_number"}"""
                val jsonElement = Json.parseToJsonElement(jsonString)

                // When
                val result = validateJsonStructure(jsonElement, SimpleClass::class)

                // Then
                expectThat(result).isNotNull().and {
                    isA<Fail.InvalidRequest>()
                    get { details }.hasSize(1)
                    get { details.first().description }.isA<InvalidRequestMessage.TypeMismatch>()
                }
            }
        }
    })
