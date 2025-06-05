package com.chesspuzzletext2sql.helpers

import com.chesspuzzletext2sql.errors.CannotConnect
import com.chesspuzzletext2sql.errors.CustomError
import com.chesspuzzletext2sql.errors.SystemError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.jetbrains.exposed.sql.transactions.transaction

fun isConnected(): Result<Boolean, CustomError> = transaction {
    try {
        Ok(!connection.isClosed)
    } catch (e: Exception) {
        Err(SystemError.CannotConnect)
    }
}
