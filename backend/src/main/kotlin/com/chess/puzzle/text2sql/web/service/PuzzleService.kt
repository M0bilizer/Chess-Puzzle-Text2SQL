package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.entities.Puzzle
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.GetRandomPuzzlesError
import com.chess.puzzle.text2sql.web.entities.helper.GetRandomPuzzlesError.Throwable
import com.chess.puzzle.text2sql.web.entities.helper.ProcessQueryError
import com.chess.puzzle.text2sql.web.entities.helper.ProcessQueryError.HibernateError
import com.chess.puzzle.text2sql.web.entities.helper.ProcessQueryError.ValidationError
import com.chess.puzzle.text2sql.web.repositories.PuzzleRepository
import com.chess.puzzle.text2sql.web.validator.SqlValidator
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * Service class responsible for querying and fetching [Puzzle] entities.
 *
 * This service provides methods to retrieve random puzzles and process SQL queries to fetch puzzles
 * from the database. It integrates with the [PuzzleRepository] for database operations and the
 * [SqlValidator] for SQL validation.
 */
@Service
class PuzzleService(
    /**
     * The [PuzzleRepository] used to interact with the database and perform CRUD operations on
     * [Puzzle] entities. This repository is responsible for fetching random puzzles and executing
     * raw SQL queries.
     */
    @Autowired private val puzzleRepository: PuzzleRepository,

    /**
     * The [SqlValidator] used to validate and sanitize raw SQL queries before execution. This
     * validator ensures that only valid and allowed SQL statements (e.g., `SELECT`) are processed
     * to prevent SQL injection and other security risks.
     */
    @Autowired private val sqlValidator: SqlValidator,
) {

    /**
     * Retrieves a list of random [Puzzle] entities from the database.
     *
     * This method uses the [PuzzleRepository] to fetch a specified number of random puzzles.
     *
     * @param n The number of random puzzles to retrieve.
     * @return A list of [Puzzle] entities, or an empty list if no puzzles are found.
     */
    fun getRandomPuzzles(n: Int): ResultWrapper<List<Puzzle>, GetRandomPuzzlesError> {
        return try {
            ResultWrapper.Success(puzzleRepository.findRandomPuzzles(n))
        } catch (e: kotlin.Throwable) {
            ResultWrapper.Failure(Throwable(e))
        }
    }

    /**
     * Processes a raw SQL query string to fetch [Puzzle] entities from the database.
     *
     * This method validates the SQL query using the [SqlValidator] and ensures that only `SELECT`
     * statements are allowed. If the query is valid and allowed, it is executed using the
     * [PuzzleRepository]. If the query is invalid or not allowed, a validation error is returned.
     * If an exception occurs during execution, a Hibernate error is returned.
     *
     * @param sqlCommand The raw SQL query string to process.
     * @return A [ResultWrapper] containing:
     *     - [ResultWrapper.Success] with a list of [Puzzle] entities if the query is successful.
     *     - [ResultWrapper.Error.ValidationError] with boolean results (`isValid` and `isAllowed`)
     *       if the query is invalid or not allowed.
     *     - [ResultWrapper.Error.HibernateError] with the exception message if an error occurs
     *       during execution.
     */
    // IMPORTANT: This method allows executing raw SQL queries. Ensure that the input
    // is sanitized to prevent SQL injection attacks.
    fun processQuery(sqlCommand: String): ResultWrapper<List<Puzzle>, ProcessQueryError> {
        val isValid = sqlValidator.isValidSql(sqlCommand)
        val isAllowed = sqlValidator.isAllowed(sqlCommand)
        if (!isValid || !isAllowed) {
            logger.warn {
                "Processing Query { sqlCommand = $sqlCommand } -> ValidationError(isValid = $isValid, isAllowed = $isAllowed)"
            }
            return ResultWrapper.Failure(ValidationError(isValid, isAllowed))
        }

        return try {
            val result = puzzleRepository.executeSqlQuery(sqlCommand)
            logger.info { "Processing Query { sqlCommand = $sqlCommand } -> OK" }
            ResultWrapper.Success(result)
        } catch (e: kotlin.Throwable) {
            logger.warn {
                "Processing Query { sqlCommand = $sqlCommand } -> HibernateError(message = ${e.message})"
            }
            ResultWrapper.Failure(HibernateError)
        }
    }
}
