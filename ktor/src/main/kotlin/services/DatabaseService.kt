package com.chesspuzzletext2sql.services

import com.chesspuzzletext2sql.tables.PuzzleTable
import com.chesspuzzletext2sql.tables.toPuzzle
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent

class DatabaseService : KoinComponent {
    fun getPuzzlesTransaction(count: Int) = transaction {
        PuzzleTable.selectAll().limit(count).map { it.toPuzzle() }
    }
}