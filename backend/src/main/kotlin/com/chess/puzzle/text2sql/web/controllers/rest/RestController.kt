package com.chess.puzzle.text2sql.web.controllers.rest

import com.chess.puzzle.text2sql.web.entities.Puzzle
import com.chess.puzzle.text2sql.web.service.LargeLanguageApiService
import com.chess.puzzle.text2sql.web.service.PuzzleService
import com.chess.puzzle.text2sql.web.service.ResultWrapper
import com.chess.puzzle.text2sql.web.support.QueryRequest
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
    @Autowired private val largeLanguageApiService: LargeLanguageApiService
) {

    @GetMapping("/api/hello")
    fun hello(): String {
        return "Hello from Spring Boot!"
    }

    @GetMapping("/api/test")
    fun test(): ResponseEntity<List<Puzzle>> {
        return ResponseEntity.ok(puzzleService.getRandomPuzzles(5))
    }

    @PostMapping("/api/query")
    fun query(@RequestBody input: QueryRequest): ResponseEntity<Any> {
        val sqlCommand = input.query
        logger.info { "Received POST on /api/query { input = $input }"}
        return when (val result = puzzleService.processQuery(sqlCommand)) {
            is ResultWrapper.Success -> ResponseEntity.ok(result.data)
            is ResultWrapper.ValidationError -> ResponseEntity.badRequest().body("ok")
            is ResultWrapper.HibernateError -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ok")
        }
    }

    @PostMapping("/api/llm")
    suspend fun llm(): ResponseEntity<String> {
        largeLanguageApiService.callDeepSeek()
        return ResponseEntity.ok("working")
    }

}