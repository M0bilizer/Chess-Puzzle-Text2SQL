package com.chess.puzzle.text2sql.web.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchMetadata(
    val query: String,
    val model: ModelName,
    val maskedQuery: String,
    val sql: String,
)
