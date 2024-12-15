package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.entities.Puzzle
import com.chess.puzzle.text2sql.web.entities.QueryRequest
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.service.PuzzleService
import com.chess.puzzle.text2sql.web.service.Text2SQLService
import com.chess.puzzle.text2sql.web.utility.ResponseUtils.failure
import com.chess.puzzle.text2sql.web.utility.ResponseUtils.success
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

/**
 * REST Controller to handle HTTP requests for the Text2SQL application.
 *
 * This controller provides endpoints for:
 * - Core application functionality (e.g., converting natural language queries to SQL).
 *
 * The controller uses various services and helpers to process requests:
 * - [PuzzleService]: Handles database interactions and puzzle processing.
 * - [Text2SQLService]: Converts natural language queries to SQL.
 */
@RestController
class Text2SqlController(
    @Autowired private val puzzleService: PuzzleService,
    @Autowired private val text2SQLService: Text2SQLService,
) {

    /**
     * Core entry point of the web application.
     *
     * Converts a natural language query into an SQL query and processes it to retrieve puzzle
     * results.
     *
     * @param input The [QueryRequest] containing the natural language query.
     * @return A [ResponseEntity] containing the puzzle results if successful, or an error message
     *   if the process fails.
     */
    @PostMapping("/api/queryPuzzle")
    suspend fun queryPuzzle(@RequestBody input: QueryRequest): ResponseEntity<String> {
        logger.info { "Received POST on /api/queryPuzzle { input = $input }" }
        val query = input.query
        val sql: String
        val puzzles: List<Puzzle>
        when (val result = text2SQLService.convertToSQL(query)) {
            is ResultWrapper.Success -> sql = result.data
            is ResultWrapper.Failure -> return failure(result.error)
        }
        when (val result = puzzleService.processQuery(sql)) {
            is ResultWrapper.Success -> puzzles = result.data
            is ResultWrapper.Failure -> return failure(result.error)
        }
        return success(puzzles)
    }
}
