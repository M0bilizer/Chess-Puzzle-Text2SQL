package com.chesspuzzletext2sql.shared.data.repositories

import com.chesspuzzletext2sql.features.puzzles.domains.Puzzle
import com.chesspuzzletext2sql.shared.data.Puzzles
import java.sql.Connection
import java.sql.ResultSet
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class PuzzleRepository(private val database: Database) {
    fun getPuzzles(limit: Int) =
        transaction(
            db = database,
            readOnly = true,
            transactionIsolation = Connection.TRANSACTION_READ_UNCOMMITTED,
        ) {
            Puzzles.selectAll().limit(limit).map { it.toPuzzle() }
        }

    fun getPuzzleById(id: String): Puzzle? =
        transaction(
            db = database,
            readOnly = true,
            transactionIsolation = Connection.TRANSACTION_READ_UNCOMMITTED,
        ) {
            Puzzles.selectAll().where { Puzzles.puzzleId eq id }.singleOrNull()?.toPuzzle()
        }

    fun selectPuzzles(query: String) =
        transaction(
            db = database,
            readOnly = true,
            transactionIsolation = Connection.TRANSACTION_READ_COMMITTED,
        ) {
            exec(query) { result ->
                generateSequence { if (result.next()) result.toPuzzle() else null }.toList()
            } ?: emptyList()
        }

    private fun ResultRow.toPuzzle(): Puzzle {
        return Puzzle(
            puzzleId = this[Puzzles.puzzleId],
            fen = this[Puzzles.fen],
            moves = this[Puzzles.moves],
            rating = this[Puzzles.rating],
            ratingDeviation = this[Puzzles.ratingDeviation],
            popularity = this[Puzzles.popularity],
            nbPlays = this[Puzzles.nbPlays],
            themes = this[Puzzles.themes],
            gameUrl = this[Puzzles.gameUrl],
            openingTags = this[Puzzles.openingTags],
        )
    }

    private fun ResultSet.toPuzzle(): Puzzle {
        return Puzzle(
            puzzleId = getString("puzzle_id"),
            fen = getString("fen"),
            moves = getString("moves"),
            rating = getInt("rating"),
            ratingDeviation = getInt("rating_deviation"),
            popularity = getInt("popularity"),
            nbPlays = getInt("nb_plays"),
            themes = getString("themes"),
            gameUrl = getString("game_url"),
            openingTags = getString("opening_tags"),
        )
    }
}
