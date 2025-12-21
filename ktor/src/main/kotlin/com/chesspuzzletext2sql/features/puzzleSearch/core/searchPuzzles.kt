package com.chesspuzzletext2sql.features.puzzleSearch.core

import com.chesspuzzletext2sql.errors.ApplicationError
import com.chesspuzzletext2sql.errors.DangerousSqlError
import com.chesspuzzletext2sql.errors.DatabaseConnectionError
import com.chesspuzzletext2sql.errors.SqlGenerationError
import com.chesspuzzletext2sql.errors.UnknownError
import com.chesspuzzletext2sql.features.puzzleSearch.data.PuzzleRepository
import com.chesspuzzletext2sql.features.puzzleSearch.models.Puzzle
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.mysql.cj.jdbc.exceptions.CommunicationsException
import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.select.Select
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun selectPuzzles(
    sql: String,
    repository: PuzzleRepository,
): Result<List<Puzzle>, ApplicationError> {
    if (!isValidSql(sql) || !isAllowed(sql)) return Err(DangerousSqlError)
    return try {
        Ok(repository.selectPuzzles(sql))
    } catch (e: Exception) {
        println(e::class.simpleName)
        when (e) {
            is ExposedSQLException -> {
                when (e.cause) {
                    is CommunicationsException -> Err(DatabaseConnectionError)
                    else -> Err(SqlGenerationError)
                }
            }

            else -> Err(UnknownError("Something when wrong when selecting for puzzles"))
        }
    }
}

private fun isValidSql(sql: String): Boolean {
    if (sql.isBlank()) return false
    return try {
        CCJSqlParserUtil.parse(sql)
        true
    } catch (e: JSQLParserException) {
        false
    }
}

private fun isAllowed(sql: String): Boolean {
    if (sql.isBlank()) return false
    if (
        !sql.trim().startsWith("SELECT", ignoreCase = true) &&
            !sql.trim().startsWith("WITH", ignoreCase = true)
    ) {
        return false
    }
    return try {
        val statement = CCJSqlParserUtil.parse(sql.trim())
        if (statement !is Select) return false
        if (sql.split(';').size > 1) return false

        val dangerousPatterns =
            listOf(
                Regex(
                    "\\b(DROP|ALTER|TRUNCATE|CREATE|INSERT|UPDATE|DELETE|EXEC|CALL)\\b",
                    RegexOption.IGNORE_CASE,
                ),
                Regex("\\b(OR\\s+1\\s*=\\s*1|UNION\\s+ALL\\s+SELECT)\\b", RegexOption.IGNORE_CASE),
            )
        if (dangerousPatterns.any { it.containsMatchIn(sql) }) return false
        true
    } catch (e: JSQLParserException) {
        false
    }
}
