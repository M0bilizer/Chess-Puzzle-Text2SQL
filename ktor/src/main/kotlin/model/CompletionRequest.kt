package com.chesspuzzletext2sql.model

import kotlinx.serialization.Serializable

@Serializable data class CompletionRequest(val message: String, val model: String)
