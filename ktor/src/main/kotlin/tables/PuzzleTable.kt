package com.chesspuzzletext2sql.tables

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object PuzzleTable : Table("T_Puzzle") {
    val id = integer("id").autoIncrement()
    val puzzleId = varchar("puzzle_id", 255)
    val fen = varchar("fen", 255)
    val moves = varchar("moves", 255)
    val rating = integer("rating")
    val ratingDeviation = integer("rating_deviation")
    val popularity = integer("popularity")
    val nbPlays = integer("nb_plays")
    val themes = varchar("themes", 255)
    val gameUrl = varchar("game_url", 255)
    val openingTags = varchar("opening_tags", 255)

    override val primaryKey = PrimaryKey(id)
}

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

fun ResultRow.toPuzzle(): Puzzle {
    return Puzzle(
        id = this[PuzzleTable.id],
        puzzleId = this[PuzzleTable.puzzleId],
        fen = this[PuzzleTable.fen],
        moves = this[PuzzleTable.moves],
        rating = this[PuzzleTable.rating],
        ratingDeviation = this[PuzzleTable.ratingDeviation],
        popularity = this[PuzzleTable.popularity],
        nbPlays = this[PuzzleTable.nbPlays],
        themes = this[PuzzleTable.themes],
        gameUrl = this[PuzzleTable.gameUrl],
        openingTags = this[PuzzleTable.openingTags],
    )
}
