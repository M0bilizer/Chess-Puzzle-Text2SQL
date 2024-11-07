package com.chess.puzzle.text2sql.web.repositories

import com.chess.puzzle.text2sql.web.entities.Puzzle

interface CustomPuzzleRepository {
    fun executeSqlQuery(sqlCommand: String): List<Puzzle>
}
