package com.chesspuzzletext2sql.model

data class LLMConfig(
    val provider: String,
    val modelName: String,
    val baseUrl: String,
    val apiKey: String,
)
