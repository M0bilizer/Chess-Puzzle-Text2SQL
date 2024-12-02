package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.entities.Puzzle
import com.chess.puzzle.text2sql.web.entities.helper.ResultWrapper
import com.chess.puzzle.text2sql.web.repositories.PuzzleRepository
import com.chess.puzzle.text2sql.web.validator.SqlValidator
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class PuzzleService(
    @Autowired private val puzzleRepository: PuzzleRepository,
    @Autowired private val sqlValidator: SqlValidator,
) {
    fun getRandomPuzzles(n: Int): List<Puzzle> {
        return puzzleRepository.findRandomPuzzles(n)
    }

    fun processQuery(sqlCommand: String): ResultWrapper<out List<Puzzle>> {
        val isValid = sqlValidator.isValidSql(sqlCommand)
        val isAllowed = sqlValidator.isAllowed(sqlCommand)
        if (!isValid || !isAllowed) {
            logger.warn { "Processing Query { sqlCommand = $sqlCommand } -> ValidationError(isValid = $isValid, isAllowed = $isAllowed)" }
            return ResultWrapper.Error.ValidationError(isValid, isAllowed)
        }

        return try {
            val result = puzzleRepository.executeSqlQuery(sqlCommand)
            logger.info { "Processing Query { sqlCommand = $sqlCommand } -> OK" }
            ResultWrapper.Success(result)
        } catch (e: Exception) {
            logger.warn { "Processing Query { sqlCommand = $sqlCommand } -> HibernateError(message = $e.message)" }
            ResultWrapper.Error.HibernateError(e.message)
        }
    }
}
