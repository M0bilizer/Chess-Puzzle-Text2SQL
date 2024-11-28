package com.chess.puzzle.text2sql.web.entities.helper

import kotlinx.serialization.Serializable

@Serializable
data class ResponseDto<T>(val status: String, val data: T)
