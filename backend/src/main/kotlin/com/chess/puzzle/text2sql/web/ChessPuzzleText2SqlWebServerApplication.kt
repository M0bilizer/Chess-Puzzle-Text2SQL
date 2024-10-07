package com.chess.puzzle.text2sql.web

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChessPuzzleText2SqlWebServerApplication {

}

fun main(args: Array<String>) {
	val dotenv = dotenv()
	println(dotenv["DB_URL"])
	System.setProperty("DB_URL", dotenv["DB_URL"])
	System.setProperty("DB_USER", dotenv["DB_USER"])
	System.setProperty("DB_PASSWORD", dotenv["DB_PASSWORD"])
	System.setProperty("AWS_ACCESS_KEY_ID",dotenv["AWS_ACCESS_KEY_ID"])
	System.setProperty("AWS_SECRET_ACCESS_KEY",dotenv["AWS_SECRET_ACCESS_KEY"])
	System.setProperty("AWS_DEFAULT_REGION",dotenv["AWS_DEFAULT_REGION"])

	runApplication<ChessPuzzleText2SqlWebServerApplication>(*args)
}
