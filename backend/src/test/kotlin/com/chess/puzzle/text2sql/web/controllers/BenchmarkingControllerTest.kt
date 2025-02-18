package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.config.FilePaths
import com.chess.puzzle.text2sql.web.domain.model.BenchmarkEntry
import com.chess.puzzle.text2sql.web.domain.model.BenchmarkResult
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.domain.model.SqlResult
import com.chess.puzzle.text2sql.web.error.GenericError
import com.chess.puzzle.text2sql.web.error.GetBenchmarkEntriesError
import com.chess.puzzle.text2sql.web.error.WriteToFileError
import com.chess.puzzle.text2sql.web.service.BenchmarkService
import com.chess.puzzle.text2sql.web.service.FileLoaderService
import com.chess.puzzle.text2sql.web.service.JsonWriterService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.IOException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BenchmarkingControllerTest {

    private val objectMapper = jacksonObjectMapper()

    private val benchmarkService: BenchmarkService = mockk()
    private val jsonWriterService: JsonWriterService = mockk()
    private val fileLoaderService: FileLoaderService = mockk()
    private val filePaths: FilePaths = mockk()

    private val benchmarkingController =
        BenchmarkingController(benchmarkService, jsonWriterService, fileLoaderService, filePaths)

    private val jsonPath = "validPath.json"

    private val benchmarkEntries =
        listOf(
            BenchmarkEntry(text = "Find puzzles with rating > 1500"),
            BenchmarkEntry(text = "Find puzzles with rating > 2000"),
        )

    private val benchmarkResults =
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

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `test benchmark success`(): Unit = runBlocking {
        val jsonString = Json.encodeToString(benchmarkResults)

        coEvery { filePaths.jsonPath } returns jsonPath
        coEvery { fileLoaderService.getBenchmarkEntries(jsonPath) } returns
            ResultWrapper.Success(benchmarkEntries)
        coEvery { benchmarkService.getBenchmark(benchmarkEntries) } returns
            ResultWrapper.Success(benchmarkResults)
        coEvery { jsonWriterService.writeToFile(any(), jsonString) } returns
            ResultWrapper.Success(Unit)

        val deferredResult = benchmarkingController.benchmark()
        val result = deferredResult.result as ResponseEntity<String>

        val expected =
            objectMapper.writeValueAsString(
                mapOf("status" to "success", "data" to benchmarkResults)
            )
        expectThat(result.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(result.body).isEqualTo(expected)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `test benchmark failure in fileLoaderService`(): Unit = runBlocking {
        val error = GetBenchmarkEntriesError.IOException(IOException())

        coEvery { filePaths.jsonPath } returns jsonPath
        coEvery { fileLoaderService.getBenchmarkEntries(jsonPath) } returns
            ResultWrapper.Failure(error)

        val deferredResult = benchmarkingController.benchmark()
        val result = deferredResult.result as ResponseEntity<String>

        val expected =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))
        expectThat(result.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(result.body).isEqualTo(expected)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `test benchmark failure in benchmarkService`(): Unit = runBlocking {
        val error = GenericError.Error

        coEvery { filePaths.jsonPath } returns jsonPath
        coEvery { fileLoaderService.getBenchmarkEntries(jsonPath) } returns
            ResultWrapper.Success(benchmarkEntries)
        coEvery { benchmarkService.getBenchmark(benchmarkEntries) } returns
            ResultWrapper.Failure(error)

        val deferredResult = benchmarkingController.benchmark()
        val result = deferredResult.result as ResponseEntity<String>

        val expected =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))
        expectThat(result.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(result.body).isEqualTo(expected)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `test benchmark failure in jsonWriterService`(): Unit = runBlocking {
        val jsonString = Json.encodeToString(benchmarkResults)
        val error = WriteToFileError.Exception(IOException())

        coEvery { filePaths.jsonPath } returns jsonPath
        coEvery { fileLoaderService.getBenchmarkEntries(jsonPath) } returns
            ResultWrapper.Success(benchmarkEntries)
        coEvery { benchmarkService.getBenchmark(benchmarkEntries) } returns
            ResultWrapper.Success(benchmarkResults)
        coEvery { jsonWriterService.writeToFile(any(), jsonString) } returns
            ResultWrapper.Failure(error)

        val deferredResult = benchmarkingController.benchmark()
        val result = deferredResult.result as ResponseEntity<String>

        val expected =
            objectMapper.writeValueAsString(mapOf("status" to "failure", "data" to error.message))
        expectThat(result.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(result.body).isEqualTo(expected)
    }
}
