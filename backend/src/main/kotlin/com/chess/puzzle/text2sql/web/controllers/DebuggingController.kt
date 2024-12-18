package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.entities.ModelName.Full
import com.chess.puzzle.text2sql.web.entities.QueryRequest
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.service.PuzzleService
import com.chess.puzzle.text2sql.web.service.Text2SQLService
import com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper
import com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper
import com.chess.puzzle.text2sql.web.utility.ResponseUtils.failure
import com.chess.puzzle.text2sql.web.utility.ResponseUtils.success
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

/**
 * REST Controller to handle debugging and health check requests for the Text2SQL application.
 *
 * This controller provides endpoints for:
 * - Debugging and health checks (e.g., checking database connectivity, sentence transformer
 *   service, etc.).
 *
 * The controller uses various services and helpers to process requests:
 * - [PuzzleService]: Handles database interactions and puzzle processing.
 * - [Text2SQLService]: Converts natural language queries to SQL.
 * - [SentenceTransformerHelper]: Handles sentence similarity tasks.
 * - [LargeLanguageApiHelper]: Interacts with the Large Language Model (LLM) API.
 */
@RestController
class DebuggingController(
    @Autowired private val text2SQLService: Text2SQLService,
    @Autowired private val puzzleService: PuzzleService,
    @Autowired private val sentenceTransformerHelper: SentenceTransformerHelper,
    @Autowired private val largeLanguageApiHelper: LargeLanguageApiHelper,
) {
    /**
     * Debugging endpoint to serve as a health check.
     *
     * @return A simple greeting message to confirm the application is running.
     */
    @GetMapping("/api/debug/hello")
    fun hello(): String {
        return "Hello from Spring Boot!"
    }

    /**
     * Debugging endpoint to check if the database is functioning correctly.
     *
     * Retrieves a random set of puzzles from the database.
     *
     * @return A [ResponseEntity] containing the retrieved puzzles if successful, or an error
     *   message if the process fails.
     */
    @GetMapping("/api/debug/db")
    fun db(): ResponseEntity<String> {
        logger.info { "Received GET on /api/debug/db" }
        return when (val result = puzzleService.getRandomPuzzles(5)) {
            is ResultWrapper.Success -> success(result.data)
            is ResultWrapper.Failure -> failure(result.error)
        }
    }

    /**
     * Debugging endpoint to check if parsing a string as SQL works correctly.
     *
     * Processes the input query as SQL and retrieves puzzle results.
     *
     * @param input The [QueryRequest] containing the SQL query.
     * @return A [ResponseEntity] containing the puzzle results if successful, or an error message
     *   if the process fails.
     */
    @PostMapping("/api/debug/sql")
    fun sql(@RequestBody input: QueryRequest): ResponseEntity<String> {
        val sqlCommand = input.query
        logger.info { "Received POST on /api/debug/sql { input = $input }" }
        return when (val result = puzzleService.processQuery(sqlCommand)) {
            is ResultWrapper.Success -> success(result.data)
            is ResultWrapper.Failure -> failure(result.error)
        }
    }

    /**
     * Debugging endpoint to check if the sentence transformer microservice is functioning
     * correctly.
     *
     * Retrieves similar demonstrations for the input query.
     *
     * @param input The [QueryRequest] containing the query.
     * @return A [ResponseEntity] containing the similar demonstrations if successful, or an error
     *   message if the process fails.
     */
    @PostMapping("/api/debug/sentenceTransformer")
    suspend fun sentenceTransformer(@RequestBody input: QueryRequest): ResponseEntity<String> {
        val request = input.query
        logger.info { "Received POST on /api/debug/sentenceTransformer { input = $input }" }
        return when (val result = sentenceTransformerHelper.getSimilarDemonstration(request)) {
            is ResultWrapper.Success -> success(result.data)
            is ResultWrapper.Failure -> failure(result.error)
        }
    }

    /**
     * Debugging endpoint to perform text-to-SQL conversion without querying the database.
     *
     * Converts the input query into an SQL query.
     *
     * @param input The [QueryRequest] containing the natural language query.
     * @return A [ResponseEntity] containing the generated SQL query if successful, or an error
     *   message if the process fails.
     */
    @PostMapping("/api/debug/text2sql")
    suspend fun text2sql(@RequestBody input: QueryRequest): ResponseEntity<String> {
        val query = input.query
        logger.info { "Received POST on /api/debug/text2sql { input = $input }" }
        return when (val result = text2SQLService.convertToSQL(query, Full)) {
            is ResultWrapper.Success -> success(result.data)
            is ResultWrapper.Failure -> failure(result.error)
        }
    }

    /**
     * Debugging endpoint to check if the application can call the Large Language Model (LLM) API.
     *
     * Sends the input prompt to the LLM API.
     *
     * @param input The [QueryRequest] containing the prompt.
     * @return A [ResponseEntity] containing the LLM response if successful, or an error message if
     *   the process fails.
     */
    @PostMapping("/api/debug/llm")
    suspend fun llm(@RequestBody input: QueryRequest): ResponseEntity<String> {
        val prompt = input.query
        logger.info { "Received POST on /api/debug/llm { input = $input }" }
        return when (val result = largeLanguageApiHelper.callDeepSeek(prompt)) {
            is ResultWrapper.Success -> success(result.data)
            is ResultWrapper.Failure -> failure(result.error)
        }
    }
}
