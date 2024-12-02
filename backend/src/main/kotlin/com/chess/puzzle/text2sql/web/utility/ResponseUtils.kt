package com.chess.puzzle.text2sql.web.utility

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.ResponseEntity

object ResponseUtils {
    private val objectMapper = jacksonObjectMapper()

    fun <T> success(data: T): ResponseEntity<String> {
        val response = mapOf("status" to "success", "data" to data)
        return ResponseEntity.ok(objectMapper.writeValueAsString(response))
    }

    fun <T> error(data: T): ResponseEntity<String> {
        val response = mapOf("status" to "error", "data" to data)
        return ResponseEntity.ok(objectMapper.writeValueAsString(response))
    }
}
