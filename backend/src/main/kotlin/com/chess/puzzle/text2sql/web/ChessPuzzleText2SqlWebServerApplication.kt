package com.chess.puzzle.text2sql.web

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChessPuzzleText2SqlWebServerApplication

fun main(args: Array<String>) {
	runApplication<ChessPuzzleText2SqlWebServerApplication>(*args)
}
