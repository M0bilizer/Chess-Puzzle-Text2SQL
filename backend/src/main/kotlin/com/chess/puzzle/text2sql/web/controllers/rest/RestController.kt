package com.chess.puzzle.text2sql.web.controllers.rest

import com.chess.puzzle.text2sql.web.entities.Puzzle
import com.chess.puzzle.text2sql.web.service.LargeLanguageApiService
import com.chess.puzzle.text2sql.web.service.PuzzleService
import com.chess.puzzle.text2sql.web.support.QueryRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

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
    fun query(@RequestBody input: QueryRequest): ResponseEntity<List<Puzzle>> {
        val sqlCommand = input.query
        return try {
            ResponseEntity.ok(puzzleService.executeSqlCommand(sqlCommand))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().body(emptyList())
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptyList())
        }
    }

    @PostMapping("/api/llm")
    suspend fun llm(): ResponseEntity<String> {
        largeLanguageApiService.callDeepSeek()
        return ResponseEntity.ok("working")
    }

}