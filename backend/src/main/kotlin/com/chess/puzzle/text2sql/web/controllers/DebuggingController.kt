package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.domain.input.GenericRequest
import com.chess.puzzle.text2sql.web.domain.input.LlmInput
import com.chess.puzzle.text2sql.web.domain.input.LlmRequest
import com.chess.puzzle.text2sql.web.domain.input.Text2SqlInput
import com.chess.puzzle.text2sql.web.domain.input.Text2SqlRequest
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.service.PuzzleService
import com.chess.puzzle.text2sql.web.service.Text2SQLService
import com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper
import com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper
import com.chess.puzzle.text2sql.web.utility.ResponseUtils.badRequest
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
        logger.info { "Received GET on /api/debug/hello" }
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
     * @param input The [GenericRequest] containing the SQL query.
     * @return A [ResponseEntity] containing the puzzle results if successful, or an error message
     *   if the process fails.
     */
    @PostMapping("/api/debug/sql")
    fun sql(@RequestBody input: GenericRequest): ResponseEntity<String> {
        logger.info { "Received POST on /api/debug/sql { input = $input }" }
        val sqlCommand = input.query
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
     * @param input The [GenericRequest] containing the query.
     * @return A [ResponseEntity] containing the similar demonstrations if successful, or an error
     *   message if the process fails.
     */
    @PostMapping("/api/debug/sentenceTransformer")
    suspend fun sentenceTransformer(@RequestBody input: GenericRequest): ResponseEntity<String> {
        logger.info { "Received POST on /api/debug/sentenceTransformer { input = $input }" }
        val request = input.query
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
     * @param request The [Text2SqlRequest] containing the natural language query.
     * @return A [ResponseEntity] containing the generated SQL query if successful, or an error
     *   message if the process fails.
     */
    @PostMapping("/api/debug/text2sql")
    suspend fun text2sql(@RequestBody request: Text2SqlRequest): ResponseEntity<String> {
        logger.info { "Received POST on /api/debug/text2sql { request = $request }" }
        val input: Text2SqlInput
        when (val result = request.toInput()) {
            is ResultWrapper.Success -> input = result.data
            is ResultWrapper.Failure -> return badRequest(result.error)
        }
        val (query, model, modelVariant) = input
        return when (val result = text2SQLService.convertToSQL(query, model, modelVariant)) {
            is ResultWrapper.Success -> success(result.data)
            is ResultWrapper.Failure -> failure(result.error)
        }
    }

    /**
     * Debugging endpoint to check if the application can call the Large Language Model (LLM) API.
     *
     * Sends the input prompt to the LLM API.
     *
     * @param request The [LlmRequest] containing the prompt and model choice.
     * @return A [ResponseEntity] containing the LLM response if successful, or an error message if
     *   the process fails.
     */
    @PostMapping("/api/debug/llm")
    suspend fun llm(@RequestBody request: LlmRequest): ResponseEntity<String> {
        logger.info { "Received POST on /api/debug/llm { request = $request }" }
        val input: LlmInput
        when (val result = request.toInput()) {
            is ResultWrapper.Success -> input = result.data
            is ResultWrapper.Failure -> return badRequest(result.error)
        }
        val (query, model) = input
        return when (val result = largeLanguageApiHelper.callModel(query, model)) {
            is ResultWrapper.Success -> success(result.data)
            is ResultWrapper.Failure -> failure(result.error)
        }
    }
}
