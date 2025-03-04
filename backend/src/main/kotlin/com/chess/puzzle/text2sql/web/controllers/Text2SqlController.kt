package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.domain.input.QueryPuzzleInput
import com.chess.puzzle.text2sql.web.domain.input.QueryPuzzleRequest
import com.chess.puzzle.text2sql.web.domain.model.ModelVariant.Full
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.domain.model.SearchMetadata
import com.chess.puzzle.text2sql.web.entities.Puzzle
import com.chess.puzzle.text2sql.web.service.PuzzleService
import com.chess.puzzle.text2sql.web.service.Text2SQLService
import com.chess.puzzle.text2sql.web.utility.CustomLogger
import com.chess.puzzle.text2sql.web.utility.ResponseUtils.badRequest
import com.chess.puzzle.text2sql.web.utility.ResponseUtils.failure
import com.chess.puzzle.text2sql.web.utility.ResponseUtils.success
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

private val customLogger = CustomLogger.instance

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
     * @param request The [QueryPuzzleRequest] containing the natural language query.
     * @return A [ResponseEntity] containing the puzzle results if successful, or an error message
     *   if the process fails.
     */
    @PostMapping("/api/queryPuzzle")
    suspend fun queryPuzzle(@RequestBody request: QueryPuzzleRequest): ResponseEntity<String> {
        customLogger.info { "Received POST on /api/queryPuzzle { request = $request }" }
        val input: QueryPuzzleInput
        val sql: String
        val searchMetadata: SearchMetadata
        val puzzles: List<Puzzle>
        when (val result = request.toInput()) {
            is ResultWrapper.Success -> input = result.data
            is ResultWrapper.Failure -> return badRequest(result.error)
        }
        val (query, model) = input
        when (
            val result =
                customLogger.withIndent(1) { text2SQLService.convertToSQL(query, model, Full) }
        ) {
            is ResultWrapper.Success -> {
                sql = result.data
                searchMetadata = result.metadata as SearchMetadata
            }
            is ResultWrapper.Failure -> return failure(result.error)
        }
        when (val result = customLogger.withIndent(1) { puzzleService.processQuery(sql) }) {
            is ResultWrapper.Success -> puzzles = result.data
            is ResultWrapper.Failure -> return failure(result.error)
        }
        customLogger.success {
            "(success=Success(puzzles=${puzzles.take(3)}, searchMetadata=$searchMetadata)"
        }
        return success(puzzles, searchMetadata)
    }
}
