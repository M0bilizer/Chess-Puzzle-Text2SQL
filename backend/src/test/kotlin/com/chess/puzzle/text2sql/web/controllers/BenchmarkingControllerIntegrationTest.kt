package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.config.FilePaths
import com.chess.puzzle.text2sql.web.domain.model.BenchmarkEntry
import com.chess.puzzle.text2sql.web.domain.model.BenchmarkResult
import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ModelVariant
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.domain.model.SqlResult
import com.chess.puzzle.text2sql.web.error.CallLargeLanguageModelError
import com.chess.puzzle.text2sql.web.error.GetBenchmarkEntriesError
import com.chess.puzzle.text2sql.web.service.BenchmarkService
import com.chess.puzzle.text2sql.web.service.FileLoaderService
import com.chess.puzzle.text2sql.web.service.JsonWriterService
import com.chess.puzzle.text2sql.web.service.Text2SQLService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
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
            jsonPath = "benchmarkEntries.json", // Path to the file in src/test/resources
            promptTemplateMdPath = "promptTemplate.md", // Path to the file in src/test/resources
            baselinePromptTemplateMdPath =
                "baselinePromptTemplate.md", // Path to the file in src/test/resources
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

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `test benchmark success`(): Unit = runBlocking {
        for ((index, benchmarkEntry) in benchmarkEntries.withIndex()) {
            val text = benchmarkEntry.text
            val result = benchmarkSuccessResults[index]
            coEvery {
                text2SQLService.convertToSQL(text, ModelName.Deepseek, ModelVariant.Full)
            } returns ResultWrapper.Success(result.full.sql)
            coEvery {
                text2SQLService.convertToSQL(text, ModelName.Deepseek, ModelVariant.Partial)
            } returns ResultWrapper.Success(result.partial.sql)
            coEvery {
                text2SQLService.convertToSQL(text, ModelName.Deepseek, ModelVariant.Baseline)
            } returns ResultWrapper.Success(result.baseline.sql)
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

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `test benchmark failure`(): Unit = runBlocking {
        for (benchmarkEntry in benchmarkEntries) {
            val text = benchmarkEntry.text
            coEvery {
                text2SQLService.convertToSQL(text, ModelName.Deepseek, ModelVariant.Full)
            } returns ResultWrapper.Failure(CallLargeLanguageModelError.HttpError)
            coEvery {
                text2SQLService.convertToSQL(text, ModelName.Deepseek, ModelVariant.Partial)
            } returns ResultWrapper.Failure(CallLargeLanguageModelError.IOException)
            coEvery {
                text2SQLService.convertToSQL(text, ModelName.Deepseek, ModelVariant.Baseline)
            } returns ResultWrapper.Failure(CallLargeLanguageModelError.UnknownError(1, ""))
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

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `test benchmark error in getting benchmarkEntries`(): Unit = runBlocking {
        // Simulate an error by using a non-existent file path
        val invalidFilePaths =
            FilePaths(
                jsonPath = "nonExistentFile.json",
                promptTemplateMdPath = "promptTemplate.md",
                baselinePromptTemplateMdPath = "baselinePromptTemplate.md",
            )
        val controllerWithInvalidPaths =
            BenchmarkingController(
                benchmarkService,
                jsonWriterService,
                fileLoaderService,
                invalidFilePaths,
            )

        val deferredResult = controllerWithInvalidPaths.benchmark()
        val result = deferredResult.result as ResponseEntity<String>

        val expected =
            objectMapper.writeValueAsString(
                mapOf(
                    "status" to "failure",
                    "data" to GetBenchmarkEntriesError.FileNotFoundError.message,
                )
            )
        expectThat(result.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(result.body).isEqualTo(expected)
    }
}
