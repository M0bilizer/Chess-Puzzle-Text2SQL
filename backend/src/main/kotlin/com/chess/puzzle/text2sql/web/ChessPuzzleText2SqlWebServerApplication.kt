package com.chess.puzzle.text2sql.web

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChessPuzzleText2SqlWebServerApplication {

}

fun main(args: Array<String>) {
	val dotenv = dotenv()
	System.setProperty("DB_URL", dotenv["DB_URL"])
	System.setProperty("DB_USER", dotenv["DB_USER"])
	System.setProperty("DB_PASSWORD", dotenv["DB_PASSWORD"])

	runApplication<ChessPuzzleText2SqlWebServerApplication>(*args)
}
