package com.chess.puzzle.text2sql.web.entities.helper

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FastApiResponse(
    @SerialName("status") val status: String,
    @SerialName("masked_query") val maskedQuery: String,
    @SerialName("data") val data: List<Demonstration>,
)
