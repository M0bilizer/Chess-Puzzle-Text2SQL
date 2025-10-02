package com.chesspuzzletext2sql.services

import com.chesspuzzletext2sql.errors.Fail
import com.chesspuzzletext2sql.errors.InvalidParameterDetail
import com.chesspuzzletext2sql.errors.InvalidParameterMessage
import com.chesspuzzletext2sql.errors.InvalidRequestMessage
import com.chesspuzzletext2sql.services.validation.accessors.age
import com.chesspuzzletext2sql.services.validation.accessors.name
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.nesk.akkurate.Validator
import dev.nesk.akkurate.annotations.Validate
import dev.nesk.akkurate.constraints.builders.isNotEmpty
import dev.nesk.akkurate.constraints.builders.isPositive
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.ktor.http.Parameters
import io.ktor.server.routing.RoutingContext
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import strikt.api.expectThat
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEqualTo

// Test data class for complex validation scenarios
@Validate
data class TestQueryParams(val name: String, val age: Int, val active: Boolean)

enum class TestEnum {
    ACTIVE,
    INACTIVE,
}

class QueryValidatorTest :
    FunSpec({
        val queryValidator = QueryValidator()
        val mockContext = mockk<RoutingContext>()
        val mockParameters = mockk<Parameters>()

        // Reset mocks before each test :cite[3]
        beforeEach {
            clearAllMocks()
            every { mockContext.call.parameters } returns mockParameters
        }

        afterSpec { clearAllMocks() }

        context("QueryValidator") {
            context("parseQueryParameters") {
                context("with valid parameters") {
                    test("should successfully parse string parameters") {
                        // Given
                        val paramName = "testParam"
                        val expectedValue = "testValue"
                        every { mockParameters[paramName] } returns expectedValue

                        val parser: (Parameters) -> String = { params -> params[paramName]!! }

                        // When
                        val result = queryValidator.parseQueryParameters(mockContext, parser)

                        // Then
                        expectThat(result).isEqualTo(Ok(expectedValue))
                    }

                    test("should successfully parse int parameters") {
                        // Given
                        val paramName = "age"
                        val expectedValue = 25
                        every { mockParameters[paramName] } returns expectedValue.toString()

                        val parser: (Parameters) -> Int = { params -> params[paramName]!!.toInt() }

                        // When
                        val result = queryValidator.parseQueryParameters(mockContext, parser)

                        // Then
                        expectThat(result).isEqualTo(Ok(expectedValue))
                    }
                }

                context("with invalid parameters") {
                    test("should handle NumberFormatException for invalid integers") {
                        // Given
                        val paramName = "age"
                        every { mockParameters[paramName] } returns "not_a_number"

                        val parser: (Parameters) -> Int = { params -> params[paramName]!!.toInt() }

                        // When
                        val result = queryValidator.parseQueryParameters(mockContext, parser)

                        // Then
                        expectThat(result).isA<Result<Nothing, Fail.InvalidRequest>>().and {
                            get { error.details }.hasSize(1)
                            get { error.details.first().field }.isEqualTo("query_param")
                            get { error.details.first().description }
                                .isA<InvalidRequestMessage.TypeMismatch>()
                        }
                    }

                    test("should handle IllegalArgumentException for missing parameters") {
                        // Given
                        val paramName = "requiredParam"
                        every { mockParameters[paramName] } returns null

                        val parser: (Parameters) -> String = { _ ->
                            throw IllegalArgumentException("Missing required parameter")
                        }

                        // When
                        val result = queryValidator.parseQueryParameters(mockContext, parser)

                        // Then
                        expectThat(result).isA<Result<Nothing, Fail.InvalidRequest>>()
                    }
                }
            }

            context("validateBusinessLogic") {
                context("with successful validation") {
                    test("should return success result for valid data") {
                        // Given
                        val testData = TestQueryParams("John", 25, true)
                        val validator = Validator<TestQueryParams> {}

                        // When
                        val result = queryValidator.validateBusinessLogic(testData, validator)

                        // Then
                        expectThat(result).isEqualTo(Ok(testData))
                    }
                }

                context("with failed validation") {
                    test("should return InvalidParameter result for validation failures") {
                        // Given
                        val testData = TestQueryParams("", -1, true) // Invalid data
                        val violations =
                            setOf(
                                "Must not be empty" to listOf("name"),
                                "Must be positive" to listOf("age"),
                            )
                        val validator = Validator<TestQueryParams> {
                            name.isNotEmpty()
                            age.isPositive()
                        }

                        // When
                        val result = queryValidator.validateBusinessLogic(testData, validator)

                        // Then
                        expectThat(result).isA<Result<Nothing, Fail.InvalidParameter>>().and {
                            get { error.details }.hasSize(2)
                            get { error.details }.containsExactlyInAnyOrder(
                                InvalidParameterDetail(
                                    "name",
                                    InvalidParameterMessage.GenericConstraint.IsNotEmpty("name")
                                ),
                                InvalidParameterDetail(
                                    "age",
                                    InvalidParameterMessage.GenericConstraint.IsPositive("age")
                                )
                            )
                        }
                    }
                }
            }

            context("validateQuery - full integration") {
                context("with valid query and validation") {
                    test("should successfully parse, validate and transform data") {
                        // Given
                        val expectedName = "testUser"
                        val expectedAge = "30"
                        every { mockParameters["name"] } returns expectedName
                        every { mockParameters["age"] } returns expectedAge

                        val config =
                            QueryValidationConfig(
                                parser = { params ->
                                    TestQueryParams(
                                        name = params["name"]!!,
                                        age = params["age"]!!.toInt(),
                                        active = true,
                                    )
                                },
                                validator = Validator<TestQueryParams> {},
                                transform = { input -> "Transformed: ${input.name}-${input.age}" },
                            )

                        // When
                        val result = queryValidator.validateQuery(mockContext, config)

                        // Then
                        expectThat(result)
                            .isEqualTo(Ok("Transformed: $expectedName-${expectedAge.toInt()}"))
                    }
                }

                context("with parsing failure") {
                    test("should return parsing error without calling validator") {
                        // Given
                        every { mockParameters["name"] } returns null // Missing required parameter

                        val config =
                            QueryValidationConfig(
                                parser = { _ ->
                                    throw IllegalArgumentException("Missing parameter")
                                },
                                validator = Validator<TestQueryParams> {},
                                transform = { it.toString() },
                            )

                        // When
                        val result = queryValidator.validateQuery(mockContext, config)

                        // Then
                        expectThat(result).isA<Result<Nothing, Fail.InvalidRequest>>()
                    }
                }
            }

            context("QueryParsers") {

                // Data-driven testing for various parser scenarios :cite[1]
                context("parameter parsers should work correctly with data class") {
                    data class ParserTestCase<T>(
                        val name: String,
                        val parserCreator: (String) -> (Parameters) -> T,
                        val inputValue: String,
                        val expectedValue: T,
                    )

                    withData(
                        ParserTestCase(
                            name = "stringParser",
                            parserCreator = { name -> QueryParsers.stringParser(name) },
                            inputValue = "testValue",
                            expectedValue = "testValue",
                        ),
                        ParserTestCase(
                            name = "intParser",
                            parserCreator = { name -> QueryParsers.intParser(name) },
                            inputValue = "123",
                            expectedValue = 123,
                        ),
                        ParserTestCase(
                            name = "booleanParser",
                            parserCreator = { name -> QueryParsers.booleanParser(name) },
                            inputValue = "true",
                            expectedValue = true,
                        ),
                    ) { (name, parserCreator, inputValue, expectedValue) ->
                        // Given
                        val paramName = "testParam"
                        every { mockParameters[paramName] } returns inputValue

                        // When
                        val parser = parserCreator(paramName)
                        val result = parser(mockParameters)

                        // Then
                        expectThat(result).isEqualTo(expectedValue)
                    }
                }

                context("enum parser") {
                    test("should parse valid enum values case-insensitively") {
                        // Given
                        val paramName = "status"
                        every { mockParameters[paramName] } returns "active"

                        val parser = QueryParsers.enumParser<TestEnum>(paramName)

                        // When
                        val result = parser(mockParameters)

                        // Then
                        expectThat(result).isEqualTo(TestEnum.ACTIVE)
                    }
                }

                context("optional parsers") {
                    test("should return null for missing optional parameters") {
                        // Given
                        val paramName = "optionalParam"
                        every { mockParameters[paramName] } returns null

                        val parser = QueryParsers.optionalStringParser(paramName)

                        // When
                        val result = parser(mockParameters)

                        // Then
                        expectThat(result).isEqualTo(null)
                    }
                }
            }

            context("RoutingContext extension function") {
                test("should delegate to QueryValidator instance") {
                    // Given
                    val expectedName = "testUser"
                    every { mockParameters["name"] } returns expectedName

                    val config =
                        QueryValidationConfig(
                            parser = { params -> QueryParsers.stringParser("name")(params) },
                            validator = Validator<String> {},
                            transform = { it },
                        )

                    // When
                    val result = mockContext.validateQuery(config)

                    // Then
                    expectThat(result).isA<Result<String, Nothing>>()
                }
            }
        }
    })
