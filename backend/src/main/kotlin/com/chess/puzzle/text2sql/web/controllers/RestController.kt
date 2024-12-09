package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.entities.helper.QueryRequest
import com.chess.puzzle.text2sql.web.entities.helper.ResultWrapper
import com.chess.puzzle.text2sql.web.service.BenchmarkService
import com.chess.puzzle.text2sql.web.service.PuzzleService
import com.chess.puzzle.text2sql.web.service.Text2SQLService
import com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper
import com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper
import com.chess.puzzle.text2sql.web.utility.ResponseUtils.error
import com.chess.puzzle.text2sql.web.utility.ResponseUtils.success
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
class RestController(
    @Autowired private val puzzleService: PuzzleService,
    @Autowired private val text2SQLService: Text2SQLService,
    // testing purpose
    @Autowired private val benchmarkService: BenchmarkService,
    // debugging purpose
    @Autowired private val sentenceTransformerHelper: SentenceTransformerHelper,
    @Autowired private val largeLanguageApiHelper: LargeLanguageApiHelper,
) {
    @PostMapping("/api/queryPuzzle")
    suspend fun queryPuzzle(@RequestBody input: QueryRequest): ResponseEntity<String> {
        logger.info { "Received POST on /api/queryPuzzle { input = $input }" }
        return when (val sql = text2SQLService.convertToSQL((input.query))) {
            is ResultWrapper.Error -> error(null)
            is ResultWrapper.Success -> {
                when (val puzzle = puzzleService.processQuery(sql.data)) {
                    is ResultWrapper.Error -> error(null)
                    is ResultWrapper.Success -> success(puzzle.data)
                }
            }
        }
    }

    @GetMapping("/api/debug/hello")
    fun hello(): String {
        return "Hello from Spring Boot!"
    }

    @GetMapping("/api/debug/db")
    fun db(): ResponseEntity<String> {
        return success(puzzleService.getRandomPuzzles(5))
    }

    @PostMapping("/api/debug/sql")
    fun sql(@RequestBody input: QueryRequest): ResponseEntity<String> {
        val sqlCommand = input.query
        logger.info { "Received POST on /api/debug/sql { input = $input }" }
        return when (val result = puzzleService.processQuery(sqlCommand)) {
            is ResultWrapper.Success -> success(result.data)
            is ResultWrapper.Error.ValidationError -> error("Validation Error")
            is ResultWrapper.Error.HibernateError -> error("Hibernate Error")
            else -> error("not sure what went wrong")
        }
    }

    @PostMapping("/api/debug/sentenceTransformer")
    suspend fun sentenceTransformer(@RequestBody input: QueryRequest): ResponseEntity<String> {
        val request = input.query
        logger.info { "Received POST on /api/debug/sentenceTransformer { input = $input }" }
        return when (val result = sentenceTransformerHelper.getSimilarDemonstration(request)) {
            is ResultWrapper.Success -> {
                val (_, demonstrations) = result.data
                success(demonstrations)
            }
            is ResultWrapper.Error -> error("nope")
        }
    }

    @PostMapping("/api/debug/text2sql")
    suspend fun text2sql(@RequestBody input: QueryRequest): ResponseEntity<String> {
        val query = input.query
        logger.info { "Received POST on /api/debug/text2sql { input = $input }" }
        return when (val result = text2SQLService.convertToSQL(query)) {
            is ResultWrapper.Success -> success(result.data)
            is ResultWrapper.Error -> error("error")
        }
    }

    @PostMapping("/api/debug/llm")
    suspend fun llm(@RequestBody input: QueryRequest): ResponseEntity<String> {
        val prompt = input.query
        logger.info { "Received POST on /api/debug/llm { input = $input }" }
        return when (val result = largeLanguageApiHelper.callDeepSeek(prompt)) {
            is ResultWrapper.Success -> success(result.data)
            is ResultWrapper.Error -> error("error")
        }
    }

    // Benchmarking purposes
    @GetMapping("/api/benchmark")
    suspend fun benchmark(): String {
        val benchmark = benchmarkService.getBenchmark()
        val jsonString = Json.encodeToString(benchmark)
        return jsonString
    }
}
