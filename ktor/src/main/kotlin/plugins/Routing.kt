package com.chesspuzzletext2sql.plugins

import com.chesspuzzletext2sql.routes.getPromptTemplate
import com.chesspuzzletext2sql.routes.getPuzzlesRandom
import com.chesspuzzletext2sql.routes.getPuzzlesSql
import com.chesspuzzletext2sql.routes.postChatCompletions
import com.chesspuzzletext2sql.routes.postChatText2Sql
import com.chesspuzzletext2sql.routes.postPuzzlesQuery
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
  install(RequestValidation) {
    validate<String> { bodyText ->
      if (!bodyText.startsWith("Hello"))
        ValidationResult.Invalid("Body text should start with 'Hello'")
      else ValidationResult.Valid
    }
  }

  routing {
    postChatCompletions("/chat/completions")
    postChatText2Sql("/chat/text2sql")
    postPuzzlesQuery("/puzzles/query")
    getPuzzlesRandom("/puzzles/random")
    getPuzzlesSql("/puzzles/sql")
    getPromptTemplate("/promptTemplate")
    get("/hello") { call.respondText("Hello World from Ktor") }
  }
}
