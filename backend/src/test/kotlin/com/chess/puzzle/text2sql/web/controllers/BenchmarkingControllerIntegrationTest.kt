package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.config.FilePaths
import com.chess.puzzle.text2sql.web.entities.BenchmarkEntry
import com.chess.puzzle.text2sql.web.entities.BenchmarkResult
import com.chess.puzzle.text2sql.web.entities.ModelName
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.CallDeepSeekError
import com.chess.puzzle.text2sql.web.entities.helper.GetBenchmarkEntriesError
import com.chess.puzzle.text2sql.web.entities.helper.SqlResult
import com.chess.puzzle.text2sql.web.service.BenchmarkService
import com.chess.puzzle.text2sql.web.service.FileLoaderService
import com.chess.puzzle.text2sql.web.service.JsonWriterService
import com.chess.puzzle.text2sql.web.service.Text2SQLService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.coEvery
import io.mockk.mockk
import java.io.File
import java.io.IOException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BenchmarkingControllerIntegrationTest {
    private val objectMapper = jacksonObjectMapper()

    private val text2SQLService: Text2SQLService = mockk()

    private val benchmarkService: BenchmarkService = BenchmarkService(text2SQLService)
    private val jsonWriterService: JsonWriterService = JsonWriterService()
    private val fileLoaderService: FileLoaderService = FileLoaderService()
    private val filePaths: FilePaths =
        FilePaths(
            jsonPath = "test.json",
            promptTemplateMdPath = "promptTemplate.md",
            baselinePromptTemplateMdPath = "baselinePromptTemplate.md",
        )
    private val benchmarkingController =
        BenchmarkingController(benchmarkService, jsonWriterService, fileLoaderService, filePaths)

    private val benchmarkEntries =
        listOf(
            BenchmarkEntry(text = "Find puzzles with rating > 1500"),
            BenchmarkEntry(text = "Find puzzles with rating > 2000"),
        )

    private val benchmarkSuccessResults =
        listOf(
            BenchmarkResult(
                text = "Find puzzles with rating > 1500",
                full = SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 1500", status = ""),
                partial = SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 1500", status = ""),
                baseline = SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 1500", status = ""),
            ),
            BenchmarkResult(
                text = "Find puzzles with rating > 2000",
                full = SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 2000", status = ""),
                partial = SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 2000", status = ""),
                baseline = SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 2000", status = ""),
            ),
        )

    private val benchmarkFailureResults =
        listOf(
            BenchmarkResult(
                text = "Find puzzles with rating > 1500",
                full = SqlResult(sql = "ERROR", status = "0"),
                partial = SqlResult(sql = "ERROR", status = "0"),
                baseline = SqlResult(sql = "ERROR", status = "0"),
            ),
            BenchmarkResult(
                text = "Find puzzles with rating > 2000",
                full = SqlResult(sql = "ERROR", status = "0"),
                partial = SqlResult(sql = "ERROR", status = "0"),
                baseline = SqlResult(sql = "ERROR", status = "0"),
            ),
        )

    private val promptTemplate =
        "{{text0}} {{sql0}} {{text1}} {{sql1}} {{text2}} {{sql2}} {{prompt}}"
    private val baselinePromptTemplate = "{{prompt}}"

    private val tempFiles = mutableListOf<File>()

    @BeforeEach
    fun createTempFile() {
        tempFiles.add(createTemporaryJsonFile("test.json", benchmarkEntries))
        tempFiles.add(createTemporaryTextFile("promptTemplate.md", promptTemplate))
        tempFiles.add(createTemporaryTextFile("baselinePromptTemplate.md", baselinePromptTemplate))
    }

    @AfterEach
    fun tearDown() {
        tempFiles.forEach { it.delete() }
    }

    @Test
    fun `test benchmark success`(): Unit = runBlocking {
        val jsonString = Json.encodeToString(benchmarkFailureResults)

        for ((index, benchmarkEntry) in benchmarkEntries.withIndex()) {
            val text = benchmarkEntry.text
            val result = benchmarkSuccessResults[index]
            coEvery { text2SQLService.convertToSQL(text, ModelName.Full) } returns
                ResultWrapper.Success(result.full.sql)
            coEvery { text2SQLService.convertToSQL(text, ModelName.Partial) } returns
                ResultWrapper.Success(result.partial.sql)
            coEvery { text2SQLService.convertToSQL(text, ModelName.Baseline) } returns
                ResultWrapper.Success(result.baseline.sql)
        }

        val deferredResult = benchmarkingController.benchmark()
        val result = deferredResult.result as ResponseEntity<String>

        val expected =
            objectMapper.writeValueAsString(
                mapOf("status" to "success", "data" to benchmarkSuccessResults)
            )
        expectThat(result.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(result.body).isEqualTo(expected)
    }

    @Test
    fun `test benchmark failure`(): Unit = runBlocking {
        val jsonString = Json.encodeToString(benchmarkFailureResults)

        for ((index, benchmarkEntry) in benchmarkEntries.withIndex()) {
            val text = benchmarkEntry.text
            coEvery { text2SQLService.convertToSQL(text, ModelName.Full) } returns
                ResultWrapper.Failure(CallDeepSeekError.HttpError)
            coEvery { text2SQLService.convertToSQL(text, ModelName.Partial) } returns
                ResultWrapper.Failure(CallDeepSeekError.IOException)
            coEvery { text2SQLService.convertToSQL(text, ModelName.Baseline) } returns
                ResultWrapper.Failure(CallDeepSeekError.UnknownError(1, ""))
        }

        val deferredResult = benchmarkingController.benchmark()
        val result = deferredResult.result as ResponseEntity<String>

        val expected =
            objectMapper.writeValueAsString(
                mapOf("status" to "success", "data" to benchmarkFailureResults)
            )
        expectThat(result.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(result.body).isEqualTo(expected)
    }

    @Test
    fun `test benchmark error in getting benchmarkEntries`(): Unit = runBlocking {
        tempFiles.forEach { it.delete() }
        val jsonString = Json.encodeToString(benchmarkFailureResults)

        val deferredResult = benchmarkingController.benchmark()
        val result = deferredResult.result as ResponseEntity<String>

        val expected =
            objectMapper.writeValueAsString(
                mapOf(
                    "status" to "failure",
                    "data" to GetBenchmarkEntriesError.IOException(IOException()).message,
                )
            )
        expectThat(result.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(result.body).isEqualTo(expected)
    }

    private fun createTemporaryTextFile(fileName: String, content: String): File {
        val file = File(fileName)
        file.createNewFile()
        file.writeText(content)
        file.deleteOnExit()
        return file
    }

    private fun createTemporaryJsonFile(fileName: String, content: List<BenchmarkEntry>): File {
        val file = File(fileName)
        file.createNewFile()
        file.writeText(objectMapper.writeValueAsString(content))
        file.deleteOnExit()
        return file
    }
}
