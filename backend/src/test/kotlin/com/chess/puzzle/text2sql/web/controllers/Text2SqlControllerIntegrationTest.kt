package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.config.FilePaths
import com.chess.puzzle.text2sql.web.domain.input.QueryPuzzleRequest
import com.chess.puzzle.text2sql.web.domain.model.Demonstration
import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ModelVariant
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.domain.model.SearchMetadata
import com.chess.puzzle.text2sql.web.entities.Puzzle
import com.chess.puzzle.text2sql.web.error.CallLargeLanguageModelError
import com.chess.puzzle.text2sql.web.error.GetSimilarDemonstrationError
import com.chess.puzzle.text2sql.web.error.GetTextFileError
import com.chess.puzzle.text2sql.web.error.ProcessPromptError
import com.chess.puzzle.text2sql.web.error.ProcessQueryError
import com.chess.puzzle.text2sql.web.repositories.PuzzleRepository
import com.chess.puzzle.text2sql.web.service.FileLoaderService
import com.chess.puzzle.text2sql.web.service.PuzzleService
import com.chess.puzzle.text2sql.web.service.Text2SQLService
import com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper
import com.chess.puzzle.text2sql.web.service.helper.PreprocessingHelper
import com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper
import com.chess.puzzle.text2sql.web.utility.ResponseUtils
import com.chess.puzzle.text2sql.web.validator.SqlValidator
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class Text2SqlControllerIntegrationTest {
    private val puzzleRepository: PuzzleRepository = mockk()
    private val sqlValidator: SqlValidator = mockk()
    private val puzzleService = PuzzleService(puzzleRepository, sqlValidator)

    private val filePaths: FilePaths = mockk()
    private val fileLoaderService: FileLoaderService = mockk()
    private val sentenceTransformerHelper: SentenceTransformerHelper = mockk()
    private val preprocessingHelper: PreprocessingHelper = mockk()
    private val largeLanguageApiHelper: LargeLanguageApiHelper = mockk()
    private val text2SQLService =
        Text2SQLService(
            filePaths,
            fileLoaderService,
            sentenceTransformerHelper,
            preprocessingHelper,
            largeLanguageApiHelper,
        )

    private val controller = Text2SqlController(puzzleService, text2SQLService)

    @Test
    fun `test queryPuzzle success scenario`(): Unit = runBlocking {
        val query = "some natural language query"
        val queryPuzzleRequest = QueryPuzzleRequest(query)
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val demonstrations =
            listOf(
                Demonstration(
                    text = "Find puzzles with rating > 2000",
                    sql = "SELECT * FROM puzzles WHERE rating > 2000",
                )
            )
        val maskedQuery = "masked Query"
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
        val processedPrompt =
            "Find puzzles with rating > 1500 Find puzzles with rating > 2000 SELECT * FROM puzzles WHERE rating > 2000"
        val sql = "SELECT * FROM puzzles WHERE rating > 1500"

        coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns
            "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelper.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations, maskedQuery)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, demonstrations) } returns
            ResultWrapper.Success(processedPrompt)
        coEvery { largeLanguageApiHelper.callModel(processedPrompt, ModelName.Deepseek) } returns
            ResultWrapper.Success(sql)

        every { sqlValidator.isValidSql(sql) } returns true
        every { sqlValidator.isAllowed(sql) } returns true
        every { puzzleRepository.executeSqlQuery(sql) } returns puzzles

        val response: ResponseEntity<String> = controller.queryPuzzle(queryPuzzleRequest)

        val expectedMetadata = SearchMetadata(query, ModelName.Deepseek, maskedQuery, sql)
        val expectedResponse = ResponseUtils.success(puzzles, expectedMetadata)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test queryPuzzle failure in executeSqlQuery`(): Unit = runBlocking {
        val query = "some natural language query"
        val queryPuzzleRequest = QueryPuzzleRequest(query)
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val demonstrations =
            listOf(
                Demonstration(
                    text = "Find puzzles with rating > 2000",
                    sql = "SELECT * FROM puzzles WHERE rating > 2000",
                )
            )
        val maskedQuery = "masked Query"

        val processedPrompt =
            "Find puzzles with rating > 1500 Find puzzles with rating > 2000 SELECT * FROM puzzles WHERE rating > 2000"
        val sql = "SELECT * FROM puzzles WHERE rating > 1500"

        coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns
            "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelper.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations, maskedQuery)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, demonstrations) } returns
            ResultWrapper.Success(processedPrompt)
        coEvery { largeLanguageApiHelper.callModel(processedPrompt, ModelName.Deepseek) } returns
            ResultWrapper.Success(sql)

        every { sqlValidator.isValidSql(sql) } returns true
        every { sqlValidator.isAllowed(sql) } returns true
        every { puzzleRepository.executeSqlQuery(sql) } throws RuntimeException("Hibernate error")

        val response: ResponseEntity<String> = controller.queryPuzzle(queryPuzzleRequest)

        val expectedResponse = ResponseUtils.failure(ProcessQueryError.HibernateError)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test queryPuzzle failure in isAllowed`(): Unit = runBlocking {
        val query = "some natural language query"
        val queryPuzzleRequest = QueryPuzzleRequest(query)
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val demonstrations =
            listOf(
                Demonstration(
                    text = "Find puzzles with rating > 2000",
                    sql = "SELECT * FROM puzzles WHERE rating > 2000",
                )
            )
        val maskedQuery = "masked Query"

        val processedPrompt =
            "Find puzzles with rating > 1500 Find puzzles with rating > 2000 SELECT * FROM puzzles WHERE rating > 2000"
        val sql = "SELECT * FROM puzzles WHERE rating > 1500"

        coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns
            "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelper.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations, maskedQuery)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, demonstrations) } returns
            ResultWrapper.Success(processedPrompt)
        coEvery { largeLanguageApiHelper.callModel(processedPrompt, ModelName.Deepseek) } returns
            ResultWrapper.Success(sql)

        every { sqlValidator.isValidSql(sql) } returns true
        every { sqlValidator.isAllowed(sql) } returns false

        val response: ResponseEntity<String> = controller.queryPuzzle(queryPuzzleRequest)

        val expectedResponse =
            ResponseUtils.failure(
                ProcessQueryError.ValidationError(isValid = true, isAllowed = false)
            )

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test queryPuzzle failure in isValid`(): Unit = runBlocking {
        val query = "some natural language query"
        val queryPuzzleRequest = QueryPuzzleRequest(query)
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val demonstrations =
            listOf(
                Demonstration(
                    text = "Find puzzles with rating > 2000",
                    sql = "SELECT * FROM puzzles WHERE rating > 2000",
                )
            )
        val maskedQuery = "masked Query"

        val processedPrompt =
            "Find puzzles with rating > 1500 Find puzzles with rating > 2000 SELECT * FROM puzzles WHERE rating > 2000"
        val sql = "SELECT * FROM puzzles WHERE rating > 1500"

        coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns
            "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelper.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations, maskedQuery)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, demonstrations) } returns
            ResultWrapper.Success(processedPrompt)
        coEvery { largeLanguageApiHelper.callModel(processedPrompt, ModelName.Deepseek) } returns
            ResultWrapper.Success(sql)

        every { sqlValidator.isValidSql(sql) } returns false
        every { sqlValidator.isAllowed(sql) } returns true

        val response: ResponseEntity<String> = controller.queryPuzzle(queryPuzzleRequest)

        val expectedResponse =
            ResponseUtils.failure(
                ProcessQueryError.ValidationError(isValid = false, isAllowed = true)
            )

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test queryPuzzle failure in callDeepSeek`(): Unit = runBlocking {
        val query = "some natural language query"
        val queryPuzzleRequest = QueryPuzzleRequest(query)
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val demonstrations =
            listOf(
                Demonstration(
                    text = "Find puzzles with rating > 2000",
                    sql = "SELECT * FROM puzzles WHERE rating > 2000",
                )
            )
        val maskedQuery = "masked Query"

        val processedPrompt =
            "Find puzzles with rating > 1500 Find puzzles with rating > 2000 SELECT * FROM puzzles WHERE rating > 2000"

        coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns
            "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelper.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations, maskedQuery)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, demonstrations) } returns
            ResultWrapper.Success(processedPrompt)
        coEvery { largeLanguageApiHelper.callModel(processedPrompt, ModelName.Deepseek) } returns
            ResultWrapper.Failure(CallLargeLanguageModelError.RateLimitError)

        val response: ResponseEntity<String> = controller.queryPuzzle(queryPuzzleRequest)

        val expectedResponse = ResponseUtils.failure(CallLargeLanguageModelError.RateLimitError)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test queryPuzzle failure in ProcessPrompt`(): Unit = runBlocking {
        val query = "some natural language query"
        val queryPuzzleRequest = QueryPuzzleRequest(query)
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val demonstrations =
            listOf(
                Demonstration(
                    text = "Find puzzles with rating > 2000",
                    sql = "SELECT * FROM puzzles WHERE rating > 2000",
                )
            )
        val maskedQuery = "masked Query"

        coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns
            "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelper.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations, maskedQuery)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, demonstrations) } returns
            ResultWrapper.Failure(ProcessPromptError.MissingPlaceholderError)

        val response: ResponseEntity<String> = controller.queryPuzzle(queryPuzzleRequest)

        val expectedResponse = ResponseUtils.failure(ProcessPromptError.MissingPlaceholderError)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test queryPuzzle failure in getSimilarDemonstration`(): Unit = runBlocking {
        val query = "some natural language query"
        val queryPuzzleRequest = QueryPuzzleRequest(query)
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"

        coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns
            "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelper.getSimilarDemonstration(query) } returns
            ResultWrapper.Failure(GetSimilarDemonstrationError.NetworkError)

        val response: ResponseEntity<String> = controller.queryPuzzle(queryPuzzleRequest)

        val expectedResponse = ResponseUtils.failure(GetSimilarDemonstrationError.NetworkError)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test queryPuzzle failure in getTextFile`(): Unit = runBlocking {
        val query = "some natural language query"
        val queryPuzzleRequest = QueryPuzzleRequest(query)

        coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns
            "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Failure(GetTextFileError.FileNotFoundError)

        val response: ResponseEntity<String> = controller.queryPuzzle(queryPuzzleRequest)

        val expectedResponse = ResponseUtils.failure(GetTextFileError.FileNotFoundError)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }
}
