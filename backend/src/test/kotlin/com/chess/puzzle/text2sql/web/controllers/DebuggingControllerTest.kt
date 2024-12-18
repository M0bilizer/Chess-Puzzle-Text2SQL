package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.entities.*
import com.chess.puzzle.text2sql.web.entities.helper.CallDeepSeekError
import com.chess.puzzle.text2sql.web.entities.helper.GetRandomPuzzlesError
import com.chess.puzzle.text2sql.web.entities.helper.GetSimilarDemonstrationError
import com.chess.puzzle.text2sql.web.entities.helper.ProcessQueryError
import com.chess.puzzle.text2sql.web.service.PuzzleService
import com.chess.puzzle.text2sql.web.service.Text2SQLService
import com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper
import com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class DebuggingControllerTest {

    private val objectMapper = ObjectMapper()

    @Test
    fun `test hello endpoint`() {
        val response = DebuggingController(mockk(), mockk(), mockk(), mockk()).hello()
        expectThat(response).isEqualTo("Hello from Spring Boot!")
    }

    @Test
    fun `test db endpoint success`() {
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

        val puzzleService: PuzzleService = mockk {
            coEvery { getRandomPuzzles(5) } returns ResultWrapper.Success(puzzles)
        }

        val response = DebuggingController(mockk(), puzzleService, mockk(), mockk()).db()
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "success", "data" to puzzles))

        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test db endpoint failure`() {
        val errorMessage = "Database error"
        val error = GetRandomPuzzlesError.Throwable(RuntimeException(errorMessage))
        val puzzleService: PuzzleService = mockk {
            coEvery { getRandomPuzzles(5) } returns ResultWrapper.Failure(error)
        }

        val response = DebuggingController(mockk(), puzzleService, mockk(), mockk()).db()
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))

        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test sql endpoint success`() {
        val queryRequest = QueryRequest(query = "SELECT * FROM puzzles")
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

        val puzzleService: PuzzleService = mockk {
            coEvery { processQuery(queryRequest.query) } returns ResultWrapper.Success(puzzles)
        }

        val response =
            DebuggingController(mockk(), puzzleService, mockk(), mockk()).sql(queryRequest)
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "success", "data" to puzzles))

        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test sql endpoint failure`() {
        val queryRequest = QueryRequest(query = "SELECT * FROM puzzles")
        val error = ProcessQueryError.HibernateError
        val puzzleService: PuzzleService = mockk {
            coEvery { processQuery(queryRequest.query) } returns ResultWrapper.Failure(error)
        }

        val response =
            DebuggingController(mockk(), puzzleService, mockk(), mockk()).sql(queryRequest)
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))

        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test sentenceTransformer endpoint success`(): Unit = runBlocking {
        val queryRequest = QueryRequest(query = "some query")
        val similarDemonstrations =
            listOf(
                Demonstration(
                    "Dutch Defense",
                    "SELECT * FROM t_puzzle WHERE opening_tags LIKE '%Dutch_Defense%'",
                ),
                Demonstration("easy", "SELECT * FROM t_puzzle WHERE rating < 900"),
            )

        val sentenceTransformerHelper: SentenceTransformerHelper = mockk {
            coEvery { getSimilarDemonstration(queryRequest.query) } returns
                ResultWrapper.Success(similarDemonstrations)
        }

        val response =
            DebuggingController(mockk(), mockk(), sentenceTransformerHelper, mockk())
                .sentenceTransformer(queryRequest)
        val expectedResponse =
            objectMapper.writeValueAsString(
                mapOf("status" to "success", "data" to similarDemonstrations)
            )

        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test sentenceTransformer endpoint failure`(): Unit = runBlocking {
        val queryRequest = QueryRequest(query = "some query")
        val error = GetSimilarDemonstrationError.NetworkError
        val sentenceTransformerHelper: SentenceTransformerHelper = mockk {
            coEvery { getSimilarDemonstration(queryRequest.query) } returns
                ResultWrapper.Failure(error)
        }

        val response =
            DebuggingController(mockk(), mockk(), sentenceTransformerHelper, mockk())
                .sentenceTransformer(queryRequest)
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))

        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test text2sql endpoint success`(): Unit = runBlocking {
        val queryRequest = QueryRequest(query = "some natural language query")
        val sqlQuery = "SELECT * FROM puzzles"

        val text2SQLService: Text2SQLService = mockk {
            coEvery { convertToSQL(queryRequest.query, ModelName.Full) } returns
                ResultWrapper.Success(sqlQuery)
        }

        val response =
            DebuggingController(text2SQLService, mockk(), mockk(), mockk()).text2sql(queryRequest)
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "success", "data" to sqlQuery))

        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test text2sql endpoint failure`(): Unit = runBlocking {
        val queryRequest = QueryRequest(query = "some natural language query")
        val error = CallDeepSeekError.ServerError
        val text2SQLService: Text2SQLService = mockk {
            coEvery { convertToSQL(queryRequest.query, ModelName.Full) } returns
                ResultWrapper.Failure(error)
        }

        val response =
            DebuggingController(text2SQLService, mockk(), mockk(), mockk()).text2sql(queryRequest)
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))

        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test llm endpoint success`(): Unit = runBlocking {
        val queryRequest = QueryRequest(query = "some prompt")
        val llmResponse = "LLM response"

        val largeLanguageApiHelper: LargeLanguageApiHelper = mockk {
            coEvery { callDeepSeek(queryRequest.query) } returns ResultWrapper.Success(llmResponse)
        }

        val response =
            DebuggingController(mockk(), mockk(), mockk(), largeLanguageApiHelper).llm(queryRequest)
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "success", "data" to llmResponse))

        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test llm endpoint failure`(): Unit = runBlocking {
        val queryRequest = QueryRequest(query = "some prompt")
        val error = CallDeepSeekError.InsufficientBalanceError
        val largeLanguageApiHelper: LargeLanguageApiHelper = mockk {
            coEvery { callDeepSeek(queryRequest.query) } returns ResultWrapper.Failure(error)
        }

        val response =
            DebuggingController(mockk(), mockk(), mockk(), largeLanguageApiHelper).llm(queryRequest)
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))

        expectThat(response.body).isEqualTo(expectedResponse)
    }
}
