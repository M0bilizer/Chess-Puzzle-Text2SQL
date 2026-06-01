package com.chesspuzzletext2sql.features.puzzleSearch.data

import org.jetbrains.exposed.v1.core.Table

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
