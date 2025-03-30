package com.chess.puzzle.text2sql.web.utility

import com.chess.puzzle.text2sql.web.error.ClientError
import com.chess.puzzle.text2sql.web.error.SystemError
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

/**
 * Utility class for creating standardized JSON responses for HTTP response entities.
 *
 * This class provides methods to generate JSON responses with a consistent structure, ensuring that
 * all responses contain a "status" field (indicating success or error) and a "data" field
 * (containing the response payload). Note that the "status" field is different from the HTTP
 * response status.
 *
 * This utility is designed to simplify the creation of HTTP responses and maintain consistency
 * across the application.
 */
object ResponseUtils {

    /**
     * The [jacksonObjectMapper] instance used to serialize maps into JSON strings.
     *
     * This mapper is configured to work with Kotlin data classes and is used to generate the JSON
     * response strings for HTTP responses.
     */
    private val objectMapper = jacksonObjectMapper()

    /**
     * Creates a success HTTP response with a JSON payload.
     *
     * This method generates a JSON response with the "status" field set to "success" and the "data"
     * field containing the provided data. The response is serialized into a JSON string using the
     * [jacksonObjectMapper]. Note that the "status" field is different from the HTTP response
     * status.
     *
     * @param data The data to include in the response. This can be of any type.
     * @return A [ResponseEntity] containing the JSON response string.
     */
    fun <T : Any> success(data: T, metadata: Any? = null): ResponseEntity<String> {
        val response =
            if (metadata == null) {
                mapOf("status" to "success", "data" to data)
            } else {
                mapOf("status" to "success", "data" to data, "metadata" to metadata)
            }
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(response))
    }

    /**
     * Creates an error HTTP response with a JSON payload.
     *
     * This method generates a JSON response with the "status" field set to "failure" and the "data"
     * field containing the error message. The response is serialized into a JSON string using the
     * [jacksonObjectMapper]. Note that the "status" field is different from the HTTP response
     * status.
     *
     * @param systemError The custom error containing the error message to include in the response.
     * @return A [ResponseEntity] containing the JSON response string.
     */
    fun failure(systemError: SystemError): ResponseEntity<String> {
        val response = mapOf("status" to "failure", "message" to systemError.message)
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(response))
    }

    /**
     * Handles bad request scenarios by creating an error response with associated error fields and
     * messages.
     *
     * @param clientErrors List of client error objects containing the details of the errors.
     * @return A [ResponseEntity] with a 400 Bad Request status and a JSON response containing the
     *   error details.
     */
    fun badRequest(clientErrors: List<ClientError>): ResponseEntity<String> {
        val message = clientErrors.associate { it.field to it.message }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(message))
    }
}
