package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.entities.Puzzle
import com.chess.puzzle.text2sql.web.repositories.PuzzleRepository
import com.chess.puzzle.text2sql.web.validator.SqlValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException

@Service
class PuzzleService(
    @Autowired private val puzzleRepository: PuzzleRepository,
    @Autowired private val sqlValidator: SqlValidator
) {

    fun getAllPuzzles(): List<Puzzle> {
        return puzzleRepository.findAll()
    }

    fun getRandomPuzzles(n: Int): List<Puzzle> {
        return puzzleRepository.findRandomPuzzles(n)
    }

    fun executeSqlCommand(sqlCommand: String): List<Puzzle> {
        if (sqlValidator.isValidSql(sqlCommand) && sqlValidator.isAllowedCommand(sqlCommand))
            return puzzleRepository.executeSqlQuery(sqlCommand)
        else
            throw IllegalArgumentException("Invalid SQL Command")
    }
}