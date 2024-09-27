package com.chess.puzzle.text2sql.web.controllers.rest

import com.chess.puzzle.text2sql.web.entities.Puzzle
import com.chess.puzzle.text2sql.web.service.PuzzleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.lang.Exception

@RestController
class RestController(@Autowired private val puzzleService: PuzzleService) {

    @GetMapping("/api/hello")
    fun hello(): String {
        return "Hello from Spring Boot!"
    }

    @GetMapping("/api/test")
    fun test(): ResponseEntity<List<Puzzle>> {
        return ResponseEntity.ok(puzzleService.getRandomPuzzles(5))
    }

    @GetMapping("/api/query")
    fun query(@RequestBody input: String): ResponseEntity<List<Puzzle>> {
        return try {
            ResponseEntity.ok(puzzleService.executeSqlCommand(input))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().body(emptyList())
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptyList())
        }
    }
}