package com.chesspuzzletext2sql.services

import com.chesspuzzletext2sql.errors.Error
import com.chesspuzzletext2sql.errors.Failure
import com.chesspuzzletext2sql.tables.Puzzle
import com.chesspuzzletext2sql.tables.PuzzleTable
import com.chesspuzzletext2sql.tables.toPuzzle
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.oshai.kotlinlogging.KotlinLogging
import java.sql.Connection
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent

private val logger = KotlinLogging.logger {}

class DatabaseService : KoinComponent {
    fun getPuzzlesTransaction(count: Int) =
        transaction(
            readOnly = true,
            transactionIsolation = Connection.TRANSACTION_READ_UNCOMMITTED,
        ) {
            PuzzleTable.selectAll().limit(count).map { it.toPuzzle() }
        }

    fun fetchPuzzles(query: String): Result<List<Puzzle>, Failure> {
        logger.info { "Fetching puzzles with (query = $query)" }
        return try {
            val result =
                transaction(
                    readOnly = true,
                    transactionIsolation = Connection.TRANSACTION_READ_COMMITTED,
                ) {
                    maxAttempts = 1
                    queryTimeout = 5
                    exec(query) { result ->
                        generateSequence { if (result.next()) result.toPuzzle() else null }.toList()
                    } ?: emptyList()
                }
            Ok(result)
        } catch (e: Exception) {
            Err(Error.SQLException)
        }
    }
}
