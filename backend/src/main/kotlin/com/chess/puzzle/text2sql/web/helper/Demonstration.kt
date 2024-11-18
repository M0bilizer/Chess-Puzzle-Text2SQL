package com.chess.puzzle.text2sql.web.helper

import kotlinx.serialization.Serializable

@Serializable
data class Demonstration(
    val text: String,
    val sql: String,
)
