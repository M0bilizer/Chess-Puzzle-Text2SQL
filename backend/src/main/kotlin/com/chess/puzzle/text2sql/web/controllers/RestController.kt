package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.entities.helper.QueryRequest
import com.chess.puzzle.text2sql.web.entities.helper.ResultWrapper
import com.chess.puzzle.text2sql.web.service.BenchmarkService
import com.chess.puzzle.text2sql.web.service.JsonWriterService
import com.chess.puzzle.text2sql.web.service.PuzzleService
import com.chess.puzzle.text2sql.web.service.Text2SQLService
import com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper
import com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper
import com.chess.puzzle.text2sql.web.utility.ResponseUtils.error
import com.chess.puzzle.text2sql.web.utility.ResponseUtils.success
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.async.DeferredResult

private val logger = KotlinLogging.logger {}

/**
 * REST Controller to handle HTTP requests for the Text2SQL application.
 *
 * This controller provides endpoints for:
 * - Core application functionality (e.g., converting natural language queries to SQL).
 * - Debugging and health checks (e.g., checking database connectivity, sentence transformer
 *   service, etc.).
 * - Benchmarking (e.g., running performance benchmarks and saving results).
 *
 * The controller uses various services and helpers to process requests:
 * - [PuzzleService]: Handles database interactions and puzzle processing.
 * - [Text2SQLService]: Converts natural language queries to SQL.
 *
 * Additionally, various services was used for benchmarking purposes:
 * - [BenchmarkService]: Runs performance benchmarks.
 * - [JsonWriterService]: Writes benchmark results to a JSON file.
 *
 * Lastly, various services was used for debugging purposes:
 * - [SentenceTransformerHelper]: Handles sentence similarity tasks.
 * - [LargeLanguageApiHelper]: Interacts with the Large Language Model (LLM) API.
 */
@RestController
class RestController(
    @Autowired private val puzzleService: PuzzleService,
    @Autowired private val text2SQLService: Text2SQLService,
    // Testing purpose
    @Autowired private val benchmarkService: BenchmarkService,
    @Autowired private val jsonWriterService: JsonWriterService,
    // Debugging purpose
    @Autowired private val sentenceTransformerHelper: SentenceTransformerHelper,
    @Autowired private val largeLanguageApiHelper: LargeLanguageApiHelper,
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
        return when (val sql = text2SQLService.convertToSQL(input.query)) {
            is ResultWrapper.Error -> error(null)
            is ResultWrapper.Success -> {
                when (val puzzle = puzzleService.processQuery(sql.data)) {
                    is ResultWrapper.Error -> error(null)
                    is ResultWrapper.Success -> success(puzzle.data)
                }
            }
        }
    }

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
        return success(puzzleService.getRandomPuzzles(5))
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
            is ResultWrapper.Error.ValidationError -> error("Validation Error")
            is ResultWrapper.Error.HibernateError -> error("Hibernate Error")
            else -> error("Not sure what went wrong")
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
            is ResultWrapper.Success -> {
                val (_, demonstrations) = result.data
                success(demonstrations)
            }
            is ResultWrapper.Error -> error("Error retrieving similar demonstrations")
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
        return when (val result = text2SQLService.convertToSQL(query)) {
            is ResultWrapper.Success -> success(result.data)
            is ResultWrapper.Error -> error("Error converting text to SQL")
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
            is ResultWrapper.Error -> error("Error calling LLM API")
        }
    }

    /**
     * Benchmarking endpoint to start the benchmark process.
     *
     * Runs the benchmark process and saves the results to a JSON file.
     *
     * @return A [DeferredResult] containing the benchmark results if successful, or an error
     *   message if the process fails.
     */
    @GetMapping("/api/benchmark")
    suspend fun benchmark(): DeferredResult<ResponseEntity<String>> {
        val deferredResult = DeferredResult<ResponseEntity<String>>(1350000)

        runBlocking {
            try {
                val benchmark = benchmarkService.getBenchmark()
                val jsonString = Json.encodeToString(benchmark)
                val filePath = "src/main/resources/data/benchmarkResult.json"

                if (jsonWriterService.writeToFile(filePath, jsonString)) {
                    deferredResult.setResult(success(benchmark))
                } else {
                    deferredResult.setResult(error("Error writing benchmark results to file"))
                }
            } catch (e: Exception) {
                deferredResult.setErrorResult(
                    ResponseEntity.status(500).body("Error: ${e.message}")
                )
            }
        }

        return deferredResult
    }
}
