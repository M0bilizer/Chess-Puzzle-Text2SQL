package com.chess.puzzle.text2sql.web.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_PUZZLE")
data class Puzzle(
    @Id
    @Column(name = "id")
    val id: Int,
    @Column(name = "puzzle_id")
    val puzzleId: String,
    @Column(name = "fen")
    val fen: String,
    @Column(name = "moves")
    val moves: String,
    @Column(name = "rating")
    val rating: Int,
    @Column(name = "rating_deviation")
    val ratingDeviation: Int,
    @Column(name = "popularity")
    val popularity: Int,
    @Column(name = "nb_plays")
    val nbPlays: Int,
    @Column(name = "themes")
    val themes: String,
    @Column(name = "game_url")
    val gameUrl: String,
    @Column(name = "opening_tags")
    val openingTags: String
)
