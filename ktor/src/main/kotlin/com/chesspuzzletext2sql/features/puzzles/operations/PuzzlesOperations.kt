package com.chesspuzzletext2sql.features.puzzles.operations

import com.chesspuzzletext2sql.features.puzzles.domains.Puzzle
import com.chesspuzzletext2sql.features.puzzles.isAllowed
import com.chesspuzzletext2sql.features.puzzles.isValidSql
import com.chesspuzzletext2sql.shared.data.repositories.PuzzleRepository
import com.chesspuzzletext2sql.shared.errors.ApplicationError
import com.chesspuzzletext2sql.shared.errors.DangerousSqlError
import com.chesspuzzletext2sql.shared.errors.DatabaseConnectionError
import com.chesspuzzletext2sql.shared.errors.NotFoundError
import com.chesspuzzletext2sql.shared.errors.SqlGenerationError
import com.chesspuzzletext2sql.shared.errors.UnknownError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.postgresql.util.PSQLException

fun getPuzzleById(id: String, repository: PuzzleRepository): Result<Puzzle, ApplicationError> {
    return try {
        val puzzle = repository.getPuzzleById(id) ?: return Err(NotFoundError())
        Ok(puzzle)
    } catch (e: Exception) {
        when (e) {
            is ExposedSQLException -> {
                when (e.cause) {
                    // TODO: should check SQLState
                    is PSQLException -> Err(DatabaseConnectionError)
                    else -> Err(SqlGenerationError)
                }
            }

            else -> Err(UnknownError("Failed to getPuzzleById: ${e.message}"))
        }
    }
}

fun selectPuzzles(
    sql: String,
    repository: PuzzleRepository,
): Result<List<Puzzle>, ApplicationError> {
    if (!isValidSql(sql) || !isAllowed(sql)) return Err(DangerousSqlError)
    return try {
        Ok(repository.selectPuzzles(sql))
    } catch (e: Exception) {
        when (e) {
            is ExposedSQLException -> {
                when (e.cause) {
                    // TODO: should check SQLState
                    is PSQLException -> Err(DatabaseConnectionError)
                    else -> Err(SqlGenerationError)
                }
            }

            else -> Err(UnknownError("Something when wrong when selecting for puzzles"))
        }
    }
}
