package com.chess.puzzle.text2sql.web.domain.model

import com.fasterxml.jackson.annotation.JsonValue
import kotlinx.serialization.Serializable

@Serializable
enum class ModelName(@JsonValue val typeName: String) {
    Deepseek("deepseek"),
    Mistral("mistral");

    companion object {
        fun toEnum(value: String): ModelName? {
            return try {
                valueOf(value.lowercase().replaceFirstChar { it.uppercase() })
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}
