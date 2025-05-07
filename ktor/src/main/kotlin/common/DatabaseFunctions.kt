package com.chesspuzzletext2sql.common

import com.chesspuzzletext2sql.errors.CannotConnect
import com.chesspuzzletext2sql.errors.CustomError
import com.chesspuzzletext2sql.errors.SystemError
import com.chesspuzzletext2sql.tables.PuzzleTable
import com.chesspuzzletext2sql.tables.toPuzzle
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun isConnected(): Result<Boolean, CustomError> = transaction {
    try {
        Ok(!connection.isClosed)
    } catch (e: Exception) {
        Err(SystemError.CannotConnect)
    }
}

fun getPuzzlesTransaction(count: Int) = transaction {
    PuzzleTable.selectAll().limit(count).map { it.toPuzzle() }
}
