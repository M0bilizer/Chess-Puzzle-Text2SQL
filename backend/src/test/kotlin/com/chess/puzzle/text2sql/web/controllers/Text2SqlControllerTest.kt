package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.domain.input.GenericRequest
import com.chess.puzzle.text2sql.web.domain.model.ModelVariant
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.Puzzle
import com.chess.puzzle.text2sql.web.error.GetSimilarDemonstrationError
import com.chess.puzzle.text2sql.web.error.ProcessQueryError
import com.chess.puzzle.text2sql.web.service.PuzzleService
import com.chess.puzzle.text2sql.web.service.Text2SQLService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class Text2SqlControllerTest {

    private val puzzleService: PuzzleService = mockk()
    private val text2SQLService: Text2SQLService = mockk()
    private val objectMapper = ObjectMapper()

    private val controller = Text2SqlController(puzzleService, text2SQLService)

    @Test
    fun `test queryPuzzle success scenario`(): Unit = runBlocking {
        val genericRequest = GenericRequest(query = "some natural language query")
        val sqlQuery = "SELECT * FROM puzzles WHERE ..."
        val puzzles =
            listOf(
                Puzzle(
                    id = 0,
                    puzzleId = "00sHx",
                    fen = "r3k2r/1pp1nQpp/3p4/1P2p3/4P3/B1PP1b2/B5PP/5K2 b k - 0 17",
                    moves = "e8d7 a2e6 d7d8 f7f8",
                    rating = 1760,
                    ratingDeviation = 80,
                    popularity = 83,
                    nbPlays = 72,
                    themes = "mate mateIn2 middlegame short",
                    gameUrl = "https://lichess.org/yyznGmXs/black#34",
                    openingTags = "Italian_Game Italian_Game_Classical_Variation",
                )
            )

        coEvery { text2SQLService.convertToSQL(genericRequest.query, ModelVariant.Full) } returns
            ResultWrapper.Success(sqlQuery)
        coEvery { puzzleService.processQuery(sqlQuery) } returns ResultWrapper.Success(puzzles)

        val response: ResponseEntity<String> = controller.queryPuzzle(genericRequest)

        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "success", "data" to puzzles))
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test queryPuzzle failure in text2SQLService`(): Unit = runBlocking {
        val genericRequest = GenericRequest(query = "some natural language query")
        val error = GetSimilarDemonstrationError.NetworkError
        coEvery { text2SQLService.convertToSQL(genericRequest.query, ModelVariant.Full) } returns
            ResultWrapper.Failure(error)

        val response: ResponseEntity<String> = controller.queryPuzzle(genericRequest)

        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test queryPuzzle failure in puzzleService`(): Unit = runBlocking {
        val genericRequest = GenericRequest(query = "some natural language query")
        val sqlQuery = "SELECT * FROM puzzles WHERE ..."
        val error = ProcessQueryError.HibernateError
        coEvery { text2SQLService.convertToSQL(genericRequest.query, ModelVariant.Full) } returns
            ResultWrapper.Success(sqlQuery)
        coEvery { puzzleService.processQuery(sqlQuery) } returns ResultWrapper.Failure(error)

        val response: ResponseEntity<String> = controller.queryPuzzle(genericRequest)

        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }
}
