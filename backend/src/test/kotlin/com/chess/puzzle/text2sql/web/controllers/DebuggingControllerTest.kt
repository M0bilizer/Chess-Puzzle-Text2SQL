package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.domain.input.GenericRequest
import com.chess.puzzle.text2sql.web.domain.input.LlmRequest
import com.chess.puzzle.text2sql.web.domain.input.PromptTemplateRequest
import com.chess.puzzle.text2sql.web.domain.input.Text2SqlRequest
import com.chess.puzzle.text2sql.web.domain.model.Demonstration
import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ModelVariant
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.Puzzle
import com.chess.puzzle.text2sql.web.error.CallLargeLanguageModelError
import com.chess.puzzle.text2sql.web.error.GetRandomPuzzlesError
import com.chess.puzzle.text2sql.web.error.GetSimilarDemonstrationError
import com.chess.puzzle.text2sql.web.error.ProcessPromptError
import com.chess.puzzle.text2sql.web.error.ProcessQueryError
import com.chess.puzzle.text2sql.web.service.PuzzleService
import com.chess.puzzle.text2sql.web.service.Text2SQLService
import com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper
import com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper
import com.chess.puzzle.text2sql.web.utility.ResponseUtils
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class DebuggingControllerTest {

    private val objectMapper = ObjectMapper()

    @Test
    fun `test hello endpoint`() {
        val response =
            DebuggingController(mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())
                .hello()
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

        val response =
            DebuggingController(
                    filePaths = mockk(),
                    fileLoaderService = mockk(),
                    sentenceTransformerHelper = mockk(),
                    preprocessingHelper = mockk(),
                    largeLanguageApiHelper = mockk(),
                    text2SQLService = mockk(),
                    puzzleService = puzzleService,
                )
                .db()
        val expectedResponse = ResponseUtils.success(puzzles)

        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test db endpoint failure`() {
        val errorMessage = "Database error"
        val error = GetRandomPuzzlesError.Throwable(RuntimeException(errorMessage))
        val puzzleService: PuzzleService = mockk {
            coEvery { getRandomPuzzles(5) } returns ResultWrapper.Failure(error)
        }

        val response =
            DebuggingController(
                    filePaths = mockk(),
                    fileLoaderService = mockk(),
                    sentenceTransformerHelper = mockk(),
                    preprocessingHelper = mockk(),
                    largeLanguageApiHelper = mockk(),
                    text2SQLService = mockk(),
                    puzzleService = puzzleService,
                )
                .db()
        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test sql endpoint success`() {
        val genericRequest = GenericRequest(query = "SELECT * FROM puzzles")
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
            coEvery { processQuery(genericRequest.query) } returns ResultWrapper.Success(puzzles)
        }

        val response =
            DebuggingController(
                    filePaths = mockk(),
                    fileLoaderService = mockk(),
                    sentenceTransformerHelper = mockk(),
                    preprocessingHelper = mockk(),
                    largeLanguageApiHelper = mockk(),
                    text2SQLService = mockk(),
                    puzzleService = puzzleService,
                )
                .sql(genericRequest)
        val expectedResponse = ResponseUtils.success((puzzles))

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test sql endpoint failure`() {
        val genericRequest = GenericRequest(query = "SELECT * FROM puzzles")
        val error = ProcessQueryError.HibernateError
        val puzzleService: PuzzleService = mockk {
            coEvery { processQuery(genericRequest.query) } returns ResultWrapper.Failure(error)
        }

        val response =
            DebuggingController(
                    filePaths = mockk(),
                    fileLoaderService = mockk(),
                    sentenceTransformerHelper = mockk(),
                    preprocessingHelper = mockk(),
                    largeLanguageApiHelper = mockk(),
                    text2SQLService = mockk(),
                    puzzleService = puzzleService,
                )
                .sql(genericRequest)
        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test sentenceTransformer endpoint success`(): Unit = runBlocking {
        val genericRequest = GenericRequest(query = "some query")
        val similarDemonstrations =
            listOf(
                Demonstration(
                    "Dutch Defense",
                    "SELECT * FROM t_puzzle WHERE opening_tags LIKE '%Dutch_Defense%'",
                ),
                Demonstration("easy", "SELECT * FROM t_puzzle WHERE rating < 900"),
            )

        val sentenceTransformerHelper: SentenceTransformerHelper = mockk {
            coEvery { getSimilarDemonstration(genericRequest.query) } returns
                ResultWrapper.Success(similarDemonstrations)
        }

        val response =
            DebuggingController(
                    filePaths = mockk(),
                    fileLoaderService = mockk(),
                    sentenceTransformerHelper = sentenceTransformerHelper,
                    preprocessingHelper = mockk(),
                    largeLanguageApiHelper = mockk(),
                    text2SQLService = mockk(),
                    puzzleService = mockk(),
                )
                .sentenceTransformer(genericRequest)
        val expectedResponse = ResponseUtils.success(similarDemonstrations)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test sentenceTransformer endpoint failure`(): Unit = runBlocking {
        val genericRequest = GenericRequest(query = "some query")
        val error = GetSimilarDemonstrationError.NetworkError
        val sentenceTransformerHelper: SentenceTransformerHelper = mockk {
            coEvery { getSimilarDemonstration(genericRequest.query) } returns
                ResultWrapper.Failure(error)
        }

        val response =
            DebuggingController(
                    filePaths = mockk(),
                    fileLoaderService = mockk(),
                    sentenceTransformerHelper = sentenceTransformerHelper,
                    preprocessingHelper = mockk(),
                    largeLanguageApiHelper = mockk(),
                    text2SQLService = mockk(),
                    puzzleService = mockk(),
                )
                .sentenceTransformer(genericRequest)
        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test promptTemplate endpoint success`(): Unit = runBlocking {
        val query = "some prompt"
        val variant = ModelVariant.Full
        val promptTemplateRequest = PromptTemplateRequest(query, variant.toString())
        val filepath = "some/file/path"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}} {{text1}} {{sql1}} {{text2}} {{sql2}}"
        val demonstrations =
            listOf(
                Demonstration("myText0", "mySql0"),
                Demonstration("myText1", "mySql1"),
                Demonstration("myText2", "mySql2"),
            )
        val processedPrompt =
            "$query ${demonstrations[0].text} ${demonstrations[0].sql} ${demonstrations[1].text} ${demonstrations[1].sql} ${demonstrations[2].text} ${demonstrations[2].sql}"

        val response =
            DebuggingController(
                    filePaths = mockk { every { getPromptTemplate(variant) } returns filepath },
                    fileLoaderService =
                        mockk {
                            every { getTextFile(filepath) } returns
                                ResultWrapper.Success(promptTemplate)
                        },
                    sentenceTransformerHelper =
                        mockk {
                            coEvery { getSimilarDemonstration(query) } returns
                                ResultWrapper.Success(demonstrations)
                        },
                    preprocessingHelper =
                        mockk {
                            every { processPrompt(query, promptTemplate, demonstrations) } returns
                                ResultWrapper.Success(processedPrompt)
                        },
                    largeLanguageApiHelper = mockk(),
                    text2SQLService = mockk(),
                    puzzleService = mockk(),
                )
                .promptTemplate(promptTemplateRequest)

        val expectedResponse = ResponseUtils.success(processedPrompt)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test promptTemplate endpoint failure`(): Unit = runBlocking {
        val query = "some prompt"
        val variant = ModelVariant.Full
        val promptTemplateRequest = PromptTemplateRequest(query, variant.toString())
        val filepath = "some/file/path"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}} {{text1}} {{sql1}} {{text2}} {{sql2}}"
        val demonstrations =
            listOf(
                Demonstration("myText0", "mySql0"),
                Demonstration("myText1", "mySql1"),
                Demonstration("myText2", "mySql2"),
            )
        val error = ProcessPromptError.InsufficientDemonstrationsError

        val response =
            DebuggingController(
                    filePaths = mockk { every { getPromptTemplate(variant) } returns filepath },
                    fileLoaderService =
                        mockk {
                            every { getTextFile(filepath) } returns
                                ResultWrapper.Success(promptTemplate)
                        },
                    sentenceTransformerHelper =
                        mockk {
                            coEvery { getSimilarDemonstration(query) } returns
                                ResultWrapper.Success(demonstrations)
                        },
                    preprocessingHelper =
                        mockk {
                            every { processPrompt(query, promptTemplate, demonstrations) } returns
                                ResultWrapper.Failure(error)
                        },
                    largeLanguageApiHelper = mockk(),
                    text2SQLService = mockk(),
                    puzzleService = mockk(),
                )
                .promptTemplate(promptTemplateRequest)

        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test llm endpoint success`(): Unit = runBlocking {
        val query = "some prompt"
        val llmRequest = LlmRequest(query)
        val llmResponse = "LLM response"

        val largeLanguageApiHelper: LargeLanguageApiHelper = mockk {
            coEvery { callModel(query, ModelName.Deepseek) } returns
                ResultWrapper.Success(llmResponse)
        }

        val response =
            DebuggingController(
                    filePaths = mockk(),
                    fileLoaderService = mockk(),
                    sentenceTransformerHelper = mockk(),
                    preprocessingHelper = mockk(),
                    largeLanguageApiHelper = largeLanguageApiHelper,
                    text2SQLService = mockk(),
                    puzzleService = mockk(),
                )
                .llm(llmRequest)
        val expectedResponse = ResponseUtils.success(llmResponse)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test llm endpoint failure`(): Unit = runBlocking {
        val query = "some prompt"
        val llmRequest = LlmRequest(query)
        val error = CallLargeLanguageModelError.InsufficientBalanceError
        val largeLanguageApiHelper: LargeLanguageApiHelper = mockk {
            coEvery { callModel(query, ModelName.Deepseek) } returns ResultWrapper.Failure(error)
        }

        val response =
            DebuggingController(
                    filePaths = mockk(),
                    fileLoaderService = mockk(),
                    sentenceTransformerHelper = mockk(),
                    preprocessingHelper = mockk(),
                    largeLanguageApiHelper = largeLanguageApiHelper,
                    text2SQLService = mockk(),
                    puzzleService = mockk(),
                )
                .llm(llmRequest)
        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test text2sql endpoint success`(): Unit = runBlocking {
        val query = "some natural language query"
        val text2SqlRequest = Text2SqlRequest(query)
        val sqlQuery = "SELECT * FROM puzzles"

        val text2SQLService: Text2SQLService = mockk {
            coEvery { convertToSQL(query, ModelName.Deepseek, ModelVariant.Full) } returns
                ResultWrapper.Success(sqlQuery)
        }

        val response =
            DebuggingController(
                    filePaths = mockk(),
                    fileLoaderService = mockk(),
                    sentenceTransformerHelper = mockk(),
                    preprocessingHelper = mockk(),
                    largeLanguageApiHelper = mockk(),
                    text2SQLService = text2SQLService,
                    puzzleService = mockk(),
                )
                .text2sql(text2SqlRequest)
        val expectedResponse = ResponseUtils.success(sqlQuery)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test text2sql endpoint success with full`(): Unit = runBlocking {
        val query = "some natural language query"
        val text2SqlRequest = Text2SqlRequest(query, modelVariant = "full")
        val sqlQuery = "SELECT * FROM puzzles"

        val text2SQLService: Text2SQLService = mockk {
            coEvery { convertToSQL(query, ModelName.Deepseek, ModelVariant.Full) } returns
                ResultWrapper.Success(sqlQuery)
        }

        val response =
            DebuggingController(
                    filePaths = mockk(),
                    fileLoaderService = mockk(),
                    sentenceTransformerHelper = mockk(),
                    preprocessingHelper = mockk(),
                    largeLanguageApiHelper = mockk(),
                    text2SQLService = text2SQLService,
                    puzzleService = mockk(),
                )
                .text2sql(text2SqlRequest)
        val expectedResponse = ResponseUtils.success(sqlQuery)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test text2sql endpoint success with partial`(): Unit = runBlocking {
        val query = "some natural language query"
        val text2SqlRequest = Text2SqlRequest(query, modelVariant = "partial")
        val sqlQuery = "SELECT * FROM puzzles"

        val text2SQLService: Text2SQLService = mockk {
            coEvery { convertToSQL(query, ModelName.Deepseek, ModelVariant.Partial) } returns
                ResultWrapper.Success(sqlQuery)
        }

        val response =
            DebuggingController(
                    filePaths = mockk(),
                    fileLoaderService = mockk(),
                    sentenceTransformerHelper = mockk(),
                    preprocessingHelper = mockk(),
                    largeLanguageApiHelper = mockk(),
                    text2SQLService = text2SQLService,
                    puzzleService = mockk(),
                )
                .text2sql(text2SqlRequest)
        val expectedResponse = ResponseUtils.success(sqlQuery)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test text2sql endpoint success with baseline`(): Unit = runBlocking {
        val query = "some natural language query"
        val text2SqlRequest = Text2SqlRequest(query, modelVariant = "baseline")
        val sqlQuery = "SELECT * FROM puzzles"

        val text2SQLService: Text2SQLService = mockk {
            coEvery { convertToSQL(query, ModelName.Deepseek, ModelVariant.Baseline) } returns
                ResultWrapper.Success(sqlQuery)
        }

        val response =
            DebuggingController(
                    filePaths = mockk(),
                    fileLoaderService = mockk(),
                    sentenceTransformerHelper = mockk(),
                    preprocessingHelper = mockk(),
                    largeLanguageApiHelper = mockk(),
                    text2SQLService = text2SQLService,
                    puzzleService = mockk(),
                )
                .text2sql(text2SqlRequest)
        val expectedResponse = ResponseUtils.success(sqlQuery)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test text2sql endpoint failure`(): Unit = runBlocking {
        val query = "some natural language query"
        val text2SqlRequest = Text2SqlRequest(query)
        val error = CallLargeLanguageModelError.ServerError
        val text2SQLService: Text2SQLService = mockk {
            coEvery { convertToSQL(query, ModelName.Deepseek, ModelVariant.Full) } returns
                ResultWrapper.Failure(error)
        }

        val response =
            DebuggingController(
                    filePaths = mockk(),
                    fileLoaderService = mockk(),
                    sentenceTransformerHelper = mockk(),
                    preprocessingHelper = mockk(),
                    largeLanguageApiHelper = mockk(),
                    text2SQLService = text2SQLService,
                    puzzleService = mockk(),
                )
                .text2sql(text2SqlRequest)
        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }
}
