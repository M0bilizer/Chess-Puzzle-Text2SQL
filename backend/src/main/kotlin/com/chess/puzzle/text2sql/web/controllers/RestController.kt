package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.entities.Puzzle
import com.chess.puzzle.text2sql.web.entities.helper.QueryRequest
import com.chess.puzzle.text2sql.web.entities.helper.ResultWrapper
import com.chess.puzzle.text2sql.web.service.PuzzleService
import com.chess.puzzle.text2sql.web.service.Text2SQLService
import com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper
import com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
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
    // debugging purpose
    @Autowired private val sentenceTransformerHelper: SentenceTransformerHelper,
    @Autowired private val languageApiHelper: LargeLanguageApiHelper,
) {
    @PostMapping("/api/queryPuzzle")
    suspend fun queryPuzzle(
        @RequestBody input: QueryRequest,
    ): ResponseEntity<List<Puzzle>> {
        logger.info { "Received POST on /api/queryPuzzle { input = $input }" }
        return when (val sql = text2SQLService.convertToSQL((input.query))) {
            is ResultWrapper.Error -> ResponseEntity.ok(emptyList())
            is ResultWrapper.Success -> {
                when (val puzzle = puzzleService.processQuery(sql.data)) {
                    is ResultWrapper.Error -> ResponseEntity.ok(emptyList())
                    is ResultWrapper.Success -> ResponseEntity.ok(puzzle.data)
                }
            }
        }
    }

    @GetMapping("/api/debug/hello")
    fun hello(): String {
        return "Hello from Spring Boot!"
    }

    @GetMapping("/api/debug/db")
    fun db(): ResponseEntity<List<Puzzle>> {
        return ResponseEntity.ok(puzzleService.getRandomPuzzles(5))
    }

    @PostMapping("/api/debug/sql")
    fun sql(
        @RequestBody input: QueryRequest,
    ): ResponseEntity<Any> {
        val sqlCommand = input.query
        logger.info { "Received POST on /api/debug/sql { input = $input }" }
        return when (val result = puzzleService.processQuery(sqlCommand)) {
            is ResultWrapper.Success -> ResponseEntity.ok(result.data)
            is ResultWrapper.Error.ValidationError -> ResponseEntity.ok("Validation Error")
            is ResultWrapper.Error.HibernateError -> ResponseEntity.ok("Hibernate Error")
            else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error")
        }
    }

    @PostMapping("/api/debug/sentenceTransformer")
    suspend fun sentenceTransformer(
        @RequestBody input: QueryRequest,
    ): ResponseEntity<Any> {
        val request = input.query
        logger.info { "Received POST on /api/debug/sentenceTransformer { input = $input }" }
        return when (val result = sentenceTransformerHelper.getSimilarDemonstration(request)) {
            is ResultWrapper.Success -> ResponseEntity.ok(result.data)
            is ResultWrapper.Error -> ResponseEntity.ok("nope")
        }
    }

    @PostMapping("/api/debug/llm")
    suspend fun llm(
        @RequestBody input: QueryRequest,
    ): ResponseEntity<Any> {
        val prompt = input.query
        logger.info { "Received POST on /api/debug/llm { input = $input }" }
        return when (val result = languageApiHelper.callDeepSeek(prompt)) {
            is ResultWrapper.Success -> ResponseEntity.ok(result.data)
            is ResultWrapper.Error -> ResponseEntity.ok("error")
        }
    }
}
