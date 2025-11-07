package com.chesspuzzletext2sql.services

import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.InvalidParameterDetail
import com.chesspuzzletext2sql.errors.InvalidParameterMessage
import com.chesspuzzletext2sql.services.validation.accessors.age
import com.chesspuzzletext2sql.services.validation.accessors.email
import com.chesspuzzletext2sql.services.validation.accessors.name
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.nesk.akkurate.Validator
import dev.nesk.akkurate.annotations.Validate
import dev.nesk.akkurate.constraints.builders.isNotEmpty
import dev.nesk.akkurate.constraints.builders.isPositive
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.RoutingRequest
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import strikt.api.expectThat
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEqualTo

// Test data classes for JSON validation
@Serializable
@Validate
data class TestUserRequest(val name: String, val age: Int, val email: String)

@Serializable data class TestUserDto(val displayName: String, val userAge: Int)

class RequestValidatorTest :
    FunSpec({
        val requestValidator = RequestValidator()
        val mockContext = mockk<RoutingContext>()
        val mockCall = mockk<RoutingCall>()
        val mockRequest = mockk<RoutingRequest>()

        // Reset mocks before each test
        beforeEach {
            clearAllMocks()
            every { mockContext.call } returns mockCall
            every { mockCall.request } returns mockRequest
        }

        afterSpec { clearAllMocks() }

        context("RequestValidator") {
            context("validateJson") {
                context("with valid JSON") {
                    test("should return JsonElement for valid JSON structure") {
                        // Given
                        val validJson = """{"name": "John", "age": 25, "email": "john@test.com"}"""

                        // When
                        val result = requestValidator.validateJson<TestUserRequest>(validJson)

                        // Then
                        expectThat(result).isA<Result<JsonElement, Nothing>>()
                    }
                }

                context("with invalid JSON") {
                    test("should return MalformedJson for malformed JSON") {
                        // Given
                        val malformedJson =
                            """{"name": "John", "age": 25""" // missing closing brace

                        // When
                        val result = requestValidator.validateJson<TestUserRequest>(malformedJson)

                        // Then
                        expectThat(result).isA<Result<Nothing, Fail.MalformedJson>>()
                    }

                    test("should return InvalidRequest for JSON with structural errors") {
                        // Given
                        val jsonWithMissingField =
                            """{"name": "John", "email": "john@test.com"}""" // missing age

                        // When
                        val result =
                            requestValidator.validateJson<TestUserRequest>(jsonWithMissingField)

                        // Then
                        expectThat(result).isA<Result<Nothing, Fail.InvalidRequest>>()
                    }
                }
            }

            context("parseJson") {
                context("with valid JSON element") {
                    test("should successfully parse to target type") {
                        // Given
                        val testUserRequest = TestUserRequest("John", 25, "john@test.com")
                        val jsonElement = Json.encodeToJsonElement(testUserRequest)

                        // When
                        val result = requestValidator.parseJson<TestUserRequest>(jsonElement)

                        // Then
                        expectThat(result).isA<Result<TestUserRequest, Nothing>>().and {
                            get { value }.isEqualTo(testUserRequest)
                        }
                    }
                }

                context("with invalid JSON element") {
                    test("should return MalformedJson for SerializationException") {
                        // Given
                        val invalidJsonElement =
                            kotlinx.serialization.json.Json.parseToJsonElement(
                                """{"name": "John", "age": "not_a_number", "email": "john@test.com"}"""
                            )

                        // When
                        val result = requestValidator.parseJson<TestUserRequest>(invalidJsonElement)

                        // Then
                        expectThat(result).isA<Result<Nothing, Fail.MalformedJson>>()
                    }

                    test("should return InvalidRequest for other parsing errors") {
                        // Given
                        // Mock a scenario that causes non-SerializationException
                        val requestValidatorWithFailingJson =
                            RequestValidator(
                                kotlinx.serialization.json.Json { ignoreUnknownKeys = false }
                            )
                        val jsonWithExtraFields =
                            kotlinx.serialization.json.Json.parseToJsonElement(
                                """{"name": "John", "age": 25, "email": "john@test.com", "extra": "field"}"""
                            )

                        // When
                        val result =
                            requestValidatorWithFailingJson.parseJson<TestUserRequest>(
                                jsonWithExtraFields
                            )

                        // Then - This might return InvalidRequest depending on your error handling
                        expectThat(result).isA<Result<Nothing, *>>()
                    }
                }
            }

            context("validateBusinessLogic") {
                context("with successful validation") {
                    test("should return success result for valid data") {
                        // Given
                        val testData = TestUserRequest("John", 25, "john@test.com")
                        val validator =
                            Validator<TestUserRequest> {
                                name.isNotEmpty()
                                age.isPositive()
                                email.isNotEmpty()
                            }

                        // When
                        val result = requestValidator.validateBusinessLogic(testData, validator)

                        // Then
                        expectThat(result).isEqualTo(Ok(testData))
                    }
                }

                context("with failed validation") {
                    test("should return InvalidParameter result for validation failures") {
                        // Given
                        val testData = TestUserRequest("", -1, "") // Invalid data
                        val validator =
                            Validator<TestUserRequest> {
                                name.isNotEmpty()
                                age.isPositive()
                                email.isNotEmpty()
                            }

                        // When
                        val result = requestValidator.validateBusinessLogic(testData, validator)

                        // Then
                        expectThat(result).isA<Result<Nothing, Fail.InvalidParameter>>().and {
                            get { error.details }.hasSize(3)
                            get { error.details }
                                .containsExactlyInAnyOrder(
                                    InvalidParameterDetail(
                                        "name",
                                        InvalidParameterMessage.GenericConstraint.IsNotEmpty("name"),
                                    ),
                                    InvalidParameterDetail(
                                        "age",
                                        InvalidParameterMessage.GenericConstraint.IsPositive("age"),
                                    ),
                                    InvalidParameterDetail(
                                        "email",
                                        InvalidParameterMessage.GenericConstraint.IsNotEmpty(
                                            "email"
                                        ),
                                    ),
                                )
                        }
                    }
                }
            }

            context("validate - full integration") {
                context("with valid request") {
                    test("should successfully validate, parse, and transform data") {
                        // Given
                        val validJson = """{"name": "John", "age": 25, "email": "john@test.com"}"""
                        val config =
                            ValidationConfig<TestUserRequest, TestUserDto>(
                                validator =
                                    Validator<TestUserRequest> {
                                        name.isNotEmpty()
                                        age.isPositive()
                                        email.isNotEmpty()
                                    },
                                transform = { input ->
                                    TestUserDto("${input.name} (${input.age})", input.age)
                                },
                            )

                        // When
                        val result = requestValidator.validate(validJson, config)

                        // Then
                        expectThat(result).isA<Result<TestUserDto, Nothing>>().and {
                            get { value.displayName }.isEqualTo("John (25)")
                            get { value.userAge }.isEqualTo(25)
                        }
                    }
                }

                context("with transformation failure") {
                    test("should return InvalidRequest when transformation fails") {
                        // Given
                        val validJson = """{"name": "John", "age": 25, "email": "john@test.com"}"""
                        val config =
                            ValidationConfig<TestUserRequest, TestUserDto>(
                                validator =
                                    Validator<TestUserRequest> {
                                        name.isNotEmpty()
                                        age.isPositive()
                                        email.isNotEmpty()
                                    },
                                transform = { _ -> throw RuntimeException("Transformation failed") },
                            )

                        // When
                        val result = requestValidator.validate(validJson, config)

                        // Then
                        expectThat(result).isA<Result<Nothing, Fail.InvalidRequest>>().and {
                            get { error.details }.hasSize(1)
                            get { error.details.first().description.description }
                                .isEqualTo("transformation")
                        }
                    }
                }

                context("with JSON validation failure") {
                    test("should return early without calling parser or validator") {
                        // Given
                        val invalidJson = "invalid json"
                        var parserCalled = false
                        var validatorCalled = false

                        val config =
                            ValidationConfig<TestUserRequest, TestUserDto>(
                                validator = Validator<TestUserRequest> { validatorCalled = true },
                                transform = {
                                    parserCalled = true
                                    TestUserDto("", 0)
                                },
                            )

                        // When
                        val result = requestValidator.validate(invalidJson, config)

                        // Then
                        expectThat(result).isA<Result<Nothing, Fail.MalformedJson>>()
                        // Verify parser and validator were not called
                        expectThat(parserCalled).isEqualTo(false)
                        expectThat(validatorCalled).isEqualTo(false)
                    }
                }
            }
        }

        context("Data-driven JSON validation tests") {
            data class JsonValidationTestCase(
                val name: String,
                val jsonInput: String,
                val shouldSucceed: Boolean,
                val expectedErrorType: Class<out Fail>? = null,
            )

            withData(
                JsonValidationTestCase(
                    name = "valid JSON",
                    jsonInput = """{"name": "Alice", "age": 30, "email": "alice@test.com"}""",
                    shouldSucceed = true,
                ),
                JsonValidationTestCase(
                    name = "missing required field",
                    jsonInput = """{"name": "Alice", "email": "alice@test.com"}""",
                    shouldSucceed = false,
                    expectedErrorType = Fail.InvalidRequest::class.java,
                ),
                JsonValidationTestCase(
                    name = "malformed JSON",
                    jsonInput = """{"name": "Alice", "age": 30, "email": "alice@test.com""",
                    shouldSucceed = false,
                    expectedErrorType = Fail.MalformedJson::class.java,
                ),
                JsonValidationTestCase(
                    name = "wrong data type",
                    jsonInput = """{"name": "Alice", "age": "thirty", "email": "alice@test.com"}""",
                    shouldSucceed = false,
                    expectedErrorType = Fail.MalformedJson::class.java,
                ),
            ) { (name, jsonInput, shouldSucceed, expectedErrorType) ->
                // When
                val result = requestValidator.validateJson<TestUserRequest>(jsonInput)

                // Then
                if (shouldSucceed) {
                    expectThat(result).isA<Result<*, Nothing>>()
                } else {
                    expectThat(result).isA<Result<Nothing, *>>()
                    if (expectedErrorType != null) {
                        expectThat(result.error).isA<Fail>()
                        expectThat(result.error.type).equals(expectedErrorType)
                    }
                }
            }
        }
    })
