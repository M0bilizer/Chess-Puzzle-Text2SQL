package com.chess.puzzle.text2sql.web.entities

/**
 * Enum class representing the different models used for benchmarking.
 * - [Full]: The full model with two features: finding similar demonstrations and schema masking.
 * - [Partial]: The partial model with one feature: finding similar demonstrations.
 * - [Baseline]: The baseline model with no additional feature.
 */
enum class ModelName {
    Full,
    Partial,
    Baseline,
}
