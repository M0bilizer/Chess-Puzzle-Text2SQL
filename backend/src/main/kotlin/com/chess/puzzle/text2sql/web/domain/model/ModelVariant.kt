package com.chess.puzzle.text2sql.web.domain.model

import com.chess.puzzle.text2sql.web.domain.model.ModelVariant.Baseline
import com.chess.puzzle.text2sql.web.domain.model.ModelVariant.Full
import com.chess.puzzle.text2sql.web.domain.model.ModelVariant.Partial
import kotlinx.serialization.Serializable

/**
 * Enum class representing the model variants used for benchmarking.
 * - [Full]: The full model with two features: finding similar demonstrations and schema masking.
 * - [Partial]: The partial model with one feature: finding similar demonstrations.
 * - [Baseline]: The baseline model with no additional feature.
 */
@Serializable
enum class ModelVariant {
    Full,
    Partial,
    Baseline;

    companion object {
        /**
         * Converts a string to a corresponding [ModelVariant] instance.
         *
         * @param value the string value to convert
         * @return the corresponding [ModelVariant] instance, or null if the value is invalid
         */
        fun toEnum(value: String): ModelVariant? {
            return try {
                valueOf(value.lowercase().replaceFirstChar { it.uppercase() })
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}
