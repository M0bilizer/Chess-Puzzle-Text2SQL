package com.chess.puzzle.text2sql.web.utility

import com.chess.puzzle.text2sql.web.error.ClientError
import com.chess.puzzle.text2sql.web.error.ProcessQueryError
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

class ResponseUtilsTest {

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `success should return a ResponseEntity with status success and provided data`() {
        val data = "Hello World"
        val responseEntity = ResponseUtils.success(data)

        expectThat(responseEntity) {
            get { statusCode }.isEqualTo(HttpStatus.OK)
            get { body }
                .isNotNull()
                .and {
                    val jsonResponse = objectMapper.readValue(responseEntity.body, Map::class.java)
                    get("status") { jsonResponse["status"] }.isEqualTo("success")
                    get("data") { jsonResponse["data"] }.isEqualTo(data)
                }
        }
    }

    @Test
    fun `error should return a ResponseEntity with status error and provided data`() {
        val data = ProcessQueryError.HibernateError
        val responseEntity = ResponseUtils.failure(data)

        expectThat(responseEntity) {
            get { statusCode }.isEqualTo(HttpStatus.OK)
            get { body }
                .isNotNull()
                .and {
                    val jsonResponse =
                        objectMapper.readValue(
                            responseEntity.body,
                            object : TypeReference<Map<String, Any>>() {},
                        )
                    get("status") { jsonResponse["status"] }.isEqualTo("failure")
                    get("data") { jsonResponse["data"] }.isEqualTo(data.message)
                }
        }
    }

    @Test
    fun `badRequest should return a ResponseEntity with status error and provided data`() {
        val errors = listOf(ClientError.InvalidModelVariant)
        val responseEntity = ResponseUtils.badRequest(errors)

        expectThat(responseEntity) {
            get { statusCode }.isEqualTo(HttpStatus.BAD_REQUEST)
            get { body }
                .isNotNull()
                .and {
                    val jsonResponse =
                        objectMapper.readValue(
                            responseEntity.body,
                            object : TypeReference<Map<String, Any>>() {},
                        )
                    for (error in errors) {
                        get(error.field) { jsonResponse[error.field] }.isEqualTo(error.message)
                    }
                }
        }
    }

    @Test
    fun `badRequest should return a ResponseEntity with many error`() {
        val errors = listOf(ClientError.InvalidModelVariant, ClientError.MissingQuery)
        val responseEntity = ResponseUtils.badRequest(errors)

        expectThat(responseEntity) {
            get { statusCode }.isEqualTo(HttpStatus.BAD_REQUEST)
            get { body }
                .isNotNull()
                .and {
                    val jsonResponse =
                        objectMapper.readValue(
                            responseEntity.body,
                            object : TypeReference<Map<String, Any>>() {},
                        )
                    for (error in errors) {
                        get(error.field) { jsonResponse[error.field] }.isEqualTo(error.message)
                    }
                }
        }
    }
}
