package com.chesspuzzletext2sql.features.puzzles.domains

import kotlinx.serialization.Serializable

@Serializable
data class Puzzle(
    val puzzleId: String,
    val fen: String,
    val moves: String,
    val rating: Int,
    val ratingDeviation: Int,
    val popularity: Int,
    val nbPlays: Int,
    val themes: String,
    val gameUrl: String,
    val openingTags: String?,
)
