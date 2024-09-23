package com.chess.puzzle.text2sql.web.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RestController {

    @GetMapping("/api/hello")
    fun hello(): String {
        return "Hello from Spring Boot!"
    }
}