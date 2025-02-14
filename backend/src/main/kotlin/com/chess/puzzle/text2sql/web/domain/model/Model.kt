package com.chess.puzzle.text2sql.web.domain.model

import kotlinx.serialization.Serializable

/**
 * Enum class representing the model variants used for benchmarking.
 * - [Full]: The full model with two features: finding similar demonstrations and schema masking.
 * - [Partial]: The partial model with one feature: finding similar demonstrations.
 * - [Baseline]: The baseline model with no additional feature.
 */
@Serializable
enum class Model {
    Default,
    Alternative;

    companion object {
        fun toEnum(value: String): Model? {
            return try {
                valueOf(value.lowercase().replaceFirstChar { it.uppercase() })
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}
