package com.chesspuzzletext2sql.features.puzzles.domains

data class LLMConfig(
    val provider: String,
    val modelName: String,
    val baseUrl: String,
    val apiKey: String,
)
