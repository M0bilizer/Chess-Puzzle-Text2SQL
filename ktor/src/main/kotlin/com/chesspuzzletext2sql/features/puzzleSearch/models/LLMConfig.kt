package com.chesspuzzletext2sql.features.puzzleSearch.models

data class LLMConfig(
    val provider: String,
    val modelName: String,
    val baseUrl: String,
    val apiKey: String,
)
