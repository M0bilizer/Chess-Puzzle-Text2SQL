package com.chesspuzzletext2sql.services

import com.chesspuzzletext2sql.errors.ClientError
import com.chesspuzzletext2sql.errors.CustomError
import com.chesspuzzletext2sql.tables.Puzzle
import com.chesspuzzletext2sql.tables.PuzzleTable
import com.chesspuzzletext2sql.tables.toPuzzle
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import java.sql.SQLException
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent

class DatabaseService : KoinComponent {
    fun getPuzzlesTransaction(count: Int) = transaction {
        PuzzleTable.selectAll().limit(count).map { it.toPuzzle() }
    }

    fun fetchPuzzles(query: String): Result<List<Puzzle>, CustomError> =
        try {
            val result = transaction {
                exec(query) { result ->
                    generateSequence { if (result.next()) result.toPuzzle() else null }.toList()
                } ?: emptyList()
            }
            Ok(result)
        } catch (e: SQLException) {
            Err(ClientError.SQLException)
        }
}
