package com.chesspuzzletext2sql.features.puzzleSearch.data

import com.chesspuzzletext2sql.features.puzzleSearch.models.Puzzle
import java.sql.Connection
import java.sql.ResultSet
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

interface PuzzleRepository {
    fun getPuzzles(limit: Int): List<Puzzle>

    fun selectPuzzles(query: String): List<Puzzle>
}

class PuzzleRepositoryImp(private val database: Database) : PuzzleRepository {

    override fun getPuzzles(limit: Int) =
        transaction(
            db = database,
            readOnly = true,
            transactionIsolation = Connection.TRANSACTION_READ_UNCOMMITTED,
        ) {
            PuzzleTable.selectAll().limit(limit).map { it.toPuzzle() }
        }

    override fun selectPuzzles(query: String) =
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

    private fun ResultSet.toPuzzle(): Puzzle {
        return Puzzle(
            id = getInt("id"),
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
