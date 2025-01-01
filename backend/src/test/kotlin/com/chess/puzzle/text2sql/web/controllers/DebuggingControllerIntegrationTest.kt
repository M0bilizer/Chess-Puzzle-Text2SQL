package com.chess.puzzle.text2sql.web.controllers

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.exception.OpenAIError
import com.aallam.openai.api.exception.PermissionException
import com.aallam.openai.api.exception.RateLimitException
import com.aallam.openai.api.exception.UnknownAPIException
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.chess.puzzle.text2sql.web.config.FilePaths
import com.chess.puzzle.text2sql.web.config.SentenceTransformerEndpoints
import com.chess.puzzle.text2sql.web.entities.*
import com.chess.puzzle.text2sql.web.entities.helper.*
import com.chess.puzzle.text2sql.web.repositories.PuzzleRepository
import com.chess.puzzle.text2sql.web.service.FileLoaderService
import com.chess.puzzle.text2sql.web.service.PuzzleService
import com.chess.puzzle.text2sql.web.service.Text2SQLService
import com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper
import com.chess.puzzle.text2sql.web.service.helper.PreprocessingHelper
import com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper
import com.chess.puzzle.text2sql.web.validator.SqlValidator
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class DebuggingControllerIntegrationTest {

    private val objectMapper = ObjectMapper()

    private val puzzleRepository: PuzzleRepository = mockk()
    private val sqlValidator: SqlValidator = mockk()
    private val puzzleService = PuzzleService(puzzleRepository, sqlValidator)

    private val filePaths: FilePaths = mockk()
    private val fileLoaderService: FileLoaderService = mockk()
    private val sentenceTransformerHelperMock: SentenceTransformerHelper = mockk()
    private val preprocessingHelper: PreprocessingHelper = mockk()
    private val largeLanguageApiHelperMock: LargeLanguageApiHelper = mockk()
    private val text2SQLService =
        Text2SQLService(
            filePaths,
            fileLoaderService,
            sentenceTransformerHelperMock,
            preprocessingHelper,
            largeLanguageApiHelperMock,
        )

    private lateinit var sentenceTransformerHelper: SentenceTransformerHelper

    private val mockOpenAI: OpenAI = mockk()
    private val largeLanguageApiHelper = LargeLanguageApiHelper(mockOpenAI)

    @Test
    fun `test hello endpoint`() {
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelperMock,
            )
        val response = controller.hello()
        expectThat(response).isEqualTo("Hello from Spring Boot!")
    }

    @Test
    fun `test db endpoint success`() {
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelperMock,
            )

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

        coEvery { puzzleRepository.findRandomPuzzles(5) } returns puzzles

        val response = controller.db()
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "success", "data" to puzzles))

        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test db endpoint failure`() {
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelperMock,
            )

        val error = GetRandomPuzzlesError.Throwable(Throwable())
        coEvery { puzzleRepository.findRandomPuzzles(5) } throws Throwable()

        val response = controller.db()
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test sql endpoint success`() {
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelperMock,
            )

        val query = "SELECT * FROM puzzles"
        val queryRequest = QueryRequest(query)
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

        coEvery { sqlValidator.isValidSql(query) } returns true
        coEvery { sqlValidator.isAllowed(query) } returns true
        coEvery { puzzleRepository.executeSqlQuery(query) } returns puzzles

        val response = controller.sql(queryRequest)
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "success", "data" to puzzles))

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test sql endpoint failure in executeSqlQuery`() {
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelperMock,
            )

        val query = "SELECT * FROM puzzles"
        val queryRequest = QueryRequest(query)
        val error = ProcessQueryError.HibernateError

        coEvery { sqlValidator.isValidSql(query) } returns true
        coEvery { sqlValidator.isAllowed(query) } returns true
        coEvery { puzzleRepository.executeSqlQuery(query) } throws Throwable()

        val response = controller.sql(queryRequest)
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test sql endpoint failure in isAllowed`() {
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelperMock,
            )

        val query = "SELECT * FROM puzzles"
        val queryRequest = QueryRequest(query)
        val error = ProcessQueryError.ValidationError(isValid = true, isAllowed = false)

        coEvery { sqlValidator.isValidSql(query) } returns true
        coEvery { sqlValidator.isAllowed(query) } returns false

        val response = controller.sql(queryRequest)
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test sql endpoint failure in isValid`() {
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelperMock,
            )

        val query = "SELECT * FROM puzzles"
        val queryRequest = QueryRequest(query)
        val error = ProcessQueryError.ValidationError(isValid = false, isAllowed = true)

        coEvery { sqlValidator.isValidSql(query) } returns false
        coEvery { sqlValidator.isAllowed(query) } returns true

        val response = controller.sql(queryRequest)
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test sentenceTransformer endpoint success`(): Unit = runBlocking {
        val expectedUrl = "http://example.com/sentence-transformer"
        val similarDemonstrations =
            listOf(
                Demonstration("text0", "sql0"),
                Demonstration("text1", "sql1"),
                Demonstration("text2", "sql2"),
            )
        val fastapiResponse =
            FastApiResponse(
                status = "success",
                maskedQuery = "masked query",
                data = similarDemonstrations,
            )

        val mockEndpoints =
            mockk<SentenceTransformerEndpoints> {
                every { sentenceTransformerUrl } returns expectedUrl
            }
        val mockEngine = MockEngine { request ->
            if (request.url.toString() == expectedUrl && request.method == HttpMethod.Post) {
                respond(
                    content = Json.encodeToString(fastapiResponse),
                    status = HttpStatusCode.OK,
                    headers =
                        headersOf("Content-Type" to listOf(ContentType.Application.Json.toString())),
                )
            } else {
                respondError(HttpStatusCode.NotFound)
            }
        }
        val client = HttpClient(mockEngine) { install(ContentNegotiation) { json() } }
        sentenceTransformerHelper = SentenceTransformerHelper(mockEndpoints, client)
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelper,
                largeLanguageApiHelper,
            )

        val queryRequest = QueryRequest(query = "some query")
        val response = controller.sentenceTransformer(queryRequest)

        val expectedResponse =
            objectMapper.writeValueAsString(
                mapOf("status" to "success", "data" to similarDemonstrations)
            )
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test sentenceTransformer endpoint network error`(): Unit = runBlocking {
        val expectedUrl = "http://example.com/sentence-transformer"

        val mockEndpoints =
            mockk<SentenceTransformerEndpoints> {
                every { sentenceTransformerUrl } returns expectedUrl
            }
        val mockEngine = MockEngine { request ->
            if (request.url.toString() == expectedUrl && request.method == HttpMethod.Post) {
                throw IOException("Connection error")
            } else {
                respondError(HttpStatusCode.NotFound)
            }
        }
        val client = HttpClient(mockEngine) { install(ContentNegotiation) { json() } }
        sentenceTransformerHelper = SentenceTransformerHelper(mockEndpoints, client)
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelper,
                largeLanguageApiHelper,
            )

        val queryRequest = QueryRequest(query = "some query")
        val response = controller.sentenceTransformer(queryRequest)

        val error = GetSimilarDemonstrationError.NetworkError
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test sentenceTransformer endpoint internal error`(): Unit = runBlocking {
        val expectedUrl = "http://example.com/sentence-transformer"
        val mockEndpoints =
            mockk<SentenceTransformerEndpoints> {
                every { sentenceTransformerUrl } returns expectedUrl
            }
        val mockEngine = MockEngine { request ->
            if (request.url.toString() == expectedUrl && request.method == HttpMethod.Post) {
                respond(
                    content =
                        Json.encodeToString(
                            FastApiResponse(
                                status = "failure",
                                maskedQuery = "masked query",
                                data = emptyList(),
                            )
                        ),
                    status = HttpStatusCode.OK,
                    headers =
                        headersOf("Content-Type" to listOf(ContentType.Application.Json.toString())),
                )
            } else {
                respondError(HttpStatusCode.NotFound)
            }
        }
        val client = HttpClient(mockEngine) { install(ContentNegotiation) { json() } }
        sentenceTransformerHelper = SentenceTransformerHelper(mockEndpoints, client)
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelper,
                largeLanguageApiHelper,
            )

        val queryRequest = QueryRequest(query = "some query")
        val response = controller.sentenceTransformer(queryRequest)

        val error = GetSimilarDemonstrationError.InternalError
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test text2sql endpoint success`(): Unit = runBlocking {
        val query = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val demonstrations =
            listOf(
                Demonstration(
                    text = "Find puzzles with rating > 2000",
                    sql = "SELECT * FROM puzzles WHERE rating > 2000",
                )
            )
        val processedPrompt =
            "Find puzzles with rating > 1500 Find puzzles with rating > 2000 SELECT * FROM puzzles WHERE rating > 2000"
        val sql = "SELECT * FROM puzzles WHERE rating > 1500"

        coEvery { filePaths.getPromptTemplate(ModelName.Full) } returns "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelperMock.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, demonstrations) } returns
            ResultWrapper.Success(processedPrompt)
        coEvery { largeLanguageApiHelperMock.callDeepSeek(processedPrompt) } returns
            ResultWrapper.Success(sql)
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelperMock,
            )

        val queryRequest = QueryRequest(query)
        val response = controller.text2sql(queryRequest)

        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "success", "data" to sql))
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test text2sql endpoint error in file loading`(): Unit = runBlocking {
        val query = "Find puzzles with rating > 1500"
        val error = GetTextFileError.IOException(IOException())

        coEvery { filePaths.getPromptTemplate(ModelName.Full) } returns "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Failure(error)
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelperMock,
            )

        val queryRequest = QueryRequest(query)
        val response = controller.text2sql(queryRequest)

        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test text2sql endpoint failure in sentence transformer`(): Unit = runBlocking {
        val query = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val error = GetSimilarDemonstrationError.InternalError

        coEvery { filePaths.getPromptTemplate(ModelName.Full) } returns "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelperMock.getSimilarDemonstration(query) } returns
            ResultWrapper.Failure(error)
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelperMock,
            )

        val queryRequest = QueryRequest(query)
        val response = controller.text2sql(queryRequest)

        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test text2sql endpoint failure in preprocessing`(): Unit = runBlocking {
        val query = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val demonstrations =
            listOf(
                Demonstration(
                    text = "Find puzzles with rating > 2000",
                    sql = "SELECT * FROM puzzles WHERE rating > 2000",
                )
            )
        val error = ProcessPromptError.MissingPlaceholderError

        coEvery { filePaths.getPromptTemplate(ModelName.Full) } returns "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelperMock.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, demonstrations) } returns
            ResultWrapper.Failure(error)
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelperMock,
            )

        val queryRequest = QueryRequest(query)
        val response = controller.text2sql(queryRequest)

        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test text2sql endpoint failure in large language api`(): Unit = runBlocking {
        val query = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val demonstrations =
            listOf(
                Demonstration(
                    text = "Find puzzles with rating > 2000",
                    sql = "SELECT * FROM puzzles WHERE rating > 2000",
                )
            )
        val processedPrompt =
            "Find puzzles with rating > 1500 Find puzzles with rating > 2000 SELECT * FROM puzzles WHERE rating > 2000"
        val error = CallDeepSeekError.InsufficientBalanceError

        coEvery { filePaths.getPromptTemplate(ModelName.Full) } returns "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelperMock.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, demonstrations) } returns
            ResultWrapper.Success(processedPrompt)
        coEvery { largeLanguageApiHelperMock.callDeepSeek(processedPrompt) } returns
            ResultWrapper.Failure(error)
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelperMock,
            )

        val queryRequest = QueryRequest(query)
        val response = controller.text2sql(queryRequest)

        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test llm endpoint success`(): Unit = runBlocking {
        val input = "My query string"
        val query = "SELECT * FROM users"
        val promptTemplate = "Convert this query to SQL: $input"
        val chatCompletion =
            ChatCompletion(
                id = "1",
                created = 1L,
                model = ModelId("deepseek-chat"),
                choices =
                    listOf(
                        ChatChoice(
                            index = 0,
                            message =
                                ChatMessage(
                                    role = ChatRole.System,
                                    messageContent = TextContent(query),
                                ),
                        )
                    ),
            )

        coEvery { mockOpenAI.chatCompletion(any()) } returns chatCompletion
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelper,
            )

        val queryRequest = QueryRequest(promptTemplate)
        val response = controller.llm(queryRequest)

        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "success", "data" to query))
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test llm endpoint failure RateLimitException`(): Unit = runBlocking {
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        coEvery { mockOpenAI.chatCompletion(any()) } throws RateLimitException(1, OpenAIError())
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelper,
            )

        val queryRequest = QueryRequest(promptTemplate)
        val response = controller.llm(queryRequest)

        val error = CallDeepSeekError.RateLimitError
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test llm endpoint failure PermissionException`(): Unit = runBlocking {
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        coEvery { mockOpenAI.chatCompletion(any()) } throws PermissionException(1, OpenAIError())
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelper,
            )

        val queryRequest = QueryRequest(promptTemplate)
        val response = controller.llm(queryRequest)

        val error = CallDeepSeekError.PermissionError
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test llm endpoint failure UnknownAPIException 402`(): Unit = runBlocking {
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        coEvery { mockOpenAI.chatCompletion(any()) } throws UnknownAPIException(402, OpenAIError())
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelper,
            )

        val queryRequest = QueryRequest(promptTemplate)
        val response = controller.llm(queryRequest)

        val error = CallDeepSeekError.InsufficientBalanceError
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test llm endpoint failure UnknownAPIException unknown`(): Unit = runBlocking {
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        coEvery { mockOpenAI.chatCompletion(any()) } throws UnknownAPIException(1, OpenAIError())
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelper,
            )

        val queryRequest = QueryRequest(promptTemplate)
        val response = controller.llm(queryRequest)

        val error = CallDeepSeekError.UnknownError(1, "no message")
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }

    @Test
    fun `test llm endpoint failure Unexpected result`(): Unit = runBlocking {
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"
        val chatCompletion =
            ChatCompletion(
                id = "1",
                created = 1L,
                model = ModelId("deepseek-chat"),
                choices =
                    listOf(
                        ChatChoice(
                            index = 0,
                            message =
                                ChatMessage(
                                    role = ChatRole.System,
                                    messageContent = mockk(), // Mocking an unexpected content type
                                ),
                        )
                    ),
            )

        coEvery { mockOpenAI.chatCompletion(any()) } returns chatCompletion
        val controller =
            DebuggingController(
                text2SQLService,
                puzzleService,
                sentenceTransformerHelperMock,
                largeLanguageApiHelper,
            )

        val queryRequest = QueryRequest(promptTemplate)
        val response = controller.llm(queryRequest)

        val error = CallDeepSeekError.UnexpectedResult
        val expectedResponse =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse)
    }
}
