package com.chess.puzzle.text2sql.web.domain.model

import com.fasterxml.jackson.annotation.JsonValue
import kotlinx.serialization.Serializable

/** Enumeration of predefined model names used. */
@Serializable
enum class ModelName(@JsonValue val typeName: String) {
    /** Represents the Deepseek model type. */
    Deepseek("deepseek"),
    /** Represents the Mistral model type. */
    Mistral("mistral");

    companion object {
        /**
         * Converts a string to a corresponding [ModelName] instance.
         *
         * @param value the string value to convert
         * @return the corresponding [ModelName] instance, or null if the value is invalid
         */
        fun toEnum(value: String): ModelName? {
            return try {
                valueOf(value.lowercase().replaceFirstChar { it.uppercase() })
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}
