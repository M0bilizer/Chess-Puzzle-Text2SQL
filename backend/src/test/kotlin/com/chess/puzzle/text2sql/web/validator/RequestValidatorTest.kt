package com.chess.puzzle.text2sql.web.validator

import com.chess.puzzle.text2sql.web.error.ClientError.InvalidModelVariant
import com.chess.puzzle.text2sql.web.error.ClientError.MissingQuery
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.isEmpty
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class RequestValidatorTest {

    @Test
    fun `RequestValidator should have no errors when all validations pass`() {
        val validator =
            RequestValidator<String> {
                isNotNull("valid value", InvalidModelVariant)
                ifPresent("valid value") {
                    isInCollection("valid value", listOf("valid value"), InvalidModelVariant)
                }
            }

        expectThat(validator) {
            get { hasErrors() }.isFalse()
            get { getErrors() }.isEmpty()
        }
    }

    @Test
    fun `RequestValidator should add error when value is null`() {
        val validator = RequestValidator<String> { isNotNull(null, MissingQuery) }

        expectThat(validator) {
            get { hasErrors() }.isTrue()
            get { getErrors() }.containsExactly(MissingQuery)
        }
    }

    @Test
    fun `RequestValidator should not add error when value is not null`() {
        val validator = RequestValidator<String> { isNotNull("valid value", MissingQuery) }

        expectThat(validator) {
            get { hasErrors() }.isFalse()
            get { getErrors() }.isEmpty()
        }
    }

    @Test
    fun `RequestValidator should add error when value is not in collection`() {
        val validator =
            RequestValidator<String> {
                ifPresent("invalid value") {
                    isInCollection("invalid value", listOf("valid value"), InvalidModelVariant)
                }
            }

        expectThat(validator) {
            get { hasErrors() }.isTrue()
            get { getErrors() }.containsExactly(InvalidModelVariant)
        }
    }

    @Test
    fun `RequestValidator should not add error when value is in collection`() {
        val validator =
            RequestValidator<String> {
                ifPresent("valid value") {
                    isInCollection("valid value", listOf("valid value"), InvalidModelVariant)
                }
            }

        expectThat(validator) {
            get { hasErrors() }.isFalse()
            get { getErrors() }.isEmpty()
        }
    }

    @Test
    fun `RequestValidator should not execute ifPresent block when value is null`() {
        val validator =
            RequestValidator<String> {
                ifPresent(null) { isInCollection("value", listOf("value"), InvalidModelVariant) }
            }

        expectThat(validator) {
            get { hasErrors() }.isFalse()
            get { getErrors() }.isEmpty()
        }
    }

    @Test
    fun `RequestValidator should handle multiple errors`() {
        val validator =
            RequestValidator<String> {
                isNotNull(null, MissingQuery)
                ifPresent("invalid value") {
                    isInCollection("invalid value", listOf("valid value"), InvalidModelVariant)
                }
            }

        expectThat(validator) {
            get { hasErrors() }.isTrue()
            get { getErrors() }.containsExactlyInAnyOrder(MissingQuery, InvalidModelVariant)
        }
    }
}
