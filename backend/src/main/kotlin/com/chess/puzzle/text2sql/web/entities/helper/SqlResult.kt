package com.chess.puzzle.text2sql.web.entities.helper

import kotlinx.serialization.Serializable

@Serializable
data class SqlResult(val sql: String, val status: String)
