package com.chesspuzzletext2sql.features.puzzles.models

import com.chesspuzzletext2sql.features.llm.models.LLMConfig
import com.chesspuzzletext2sql.features.prompts.models.PromptTemplate
import kotlinx.serialization.Serializable

@Serializable
data class Puzzle(
    val id: Int,
    val puzzleId: String,
    val fen: String,
    val moves: String,
    val rating: Int,
    val ratingDeviation: Int,
    val popularity: Int,
    val nbPlays: Int,
    val themes: String,
    val gameUrl: String,
    val openingTags: String,
)

@Serializable
data class PuzzlesQueryRequest(val query: String, val template: String, val model: String)

data class PuzzlesQueryDto(
    val query: String,
    val promptTemplate: PromptTemplate,
    val llmConfig: LLMConfig,
)

@Serializable data class PuzzleSearchResponse(val puzzles: List<Puzzle>, val generatedSql: String)
