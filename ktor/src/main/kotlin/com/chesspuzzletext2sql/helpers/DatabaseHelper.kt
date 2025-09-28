package com.chesspuzzletext2sql.helpers

import com.chesspuzzletext2sql.errors.Error
import com.chesspuzzletext2sql.errors.Failure
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.jetbrains.exposed.sql.transactions.transaction

fun isConnected(): Result<Boolean, Failure> = transaction {
    try {
        Ok(!connection.isClosed)
    } catch (e: Exception) {
        Err(Error.CannotConnectToDatabase)
    }
}
