package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.domain.model.BenchmarkEntry
import com.chess.puzzle.text2sql.web.domain.model.BenchmarkResult
import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ModelVariant
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.domain.model.SqlResult
import com.chess.puzzle.text2sql.web.error.CallLargeLanguageModelError
import com.chess.puzzle.text2sql.web.error.GetSimilarDemonstrationError
import com.chess.puzzle.text2sql.web.error.GetTextFileError
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.IOException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BenchmarkServiceTest {

    private val text2SQLService: Text2SQLService = mockk()
    private val benchmarkService = BenchmarkService(text2SQLService)

    @Test
    fun `test getBenchmark success`(): Unit = runBlocking {
        // Arrange
        val benchmarkEntries =
            listOf(
                BenchmarkEntry(text = "Find puzzles with rating > 1500"),
                BenchmarkEntry(text = "Find puzzles with rating > 2000"),
            )

        val fullSql = "SELECT * FROM puzzles WHERE rating > 1500"
        val partialSql = "SELECT * FROM puzzles WHERE rating > 1500"
        val baselineSql = "SELECT * FROM puzzles WHERE rating > 1500"

        coEvery {
            text2SQLService.convertToSQL(any(), ModelName.Deepseek, ModelVariant.Full)
        } returns ResultWrapper.Success(fullSql)
        coEvery {
            text2SQLService.convertToSQL(any(), ModelName.Deepseek, ModelVariant.Partial)
        } returns ResultWrapper.Success(partialSql)
        coEvery {
            text2SQLService.convertToSQL(any(), ModelName.Deepseek, ModelVariant.Baseline)
        } returns ResultWrapper.Success(baselineSql)

        // Act
        val result = benchmarkService.getBenchmark(benchmarkEntries)

        // Assert
        val expectedResults =
            listOf(
                BenchmarkResult(
                    text = "Find puzzles with rating > 1500",
                    full = SqlResult(sql = fullSql, status = ""),
                    partial = SqlResult(sql = partialSql, status = ""),
                    baseline = SqlResult(sql = baselineSql, status = ""),
                ),
                BenchmarkResult(
                    text = "Find puzzles with rating > 2000",
                    full = SqlResult(sql = fullSql, status = ""),
                    partial = SqlResult(sql = partialSql, status = ""),
                    baseline = SqlResult(sql = baselineSql, status = ""),
                ),
            )
        expectThat(result).isEqualTo(ResultWrapper.Success(expectedResults))
    }

    @Test
    fun `test getBenchmark failure in Full model`(): Unit = runBlocking {
        // Arrange
        val benchmarkEntries =
            listOf(
                BenchmarkEntry(text = "Find puzzles with rating > 1500"),
                BenchmarkEntry(text = "Find puzzles with rating > 2000"),
            )

        val error = GetSimilarDemonstrationError.NetworkError

        coEvery {
            text2SQLService.convertToSQL(any(), ModelName.Deepseek, ModelVariant.Full)
        } returns ResultWrapper.Failure(error)
        coEvery {
            text2SQLService.convertToSQL(any(), ModelName.Deepseek, ModelVariant.Partial)
        } returns ResultWrapper.Success("SELECT * FROM puzzles WHERE rating > 1500")
        coEvery {
            text2SQLService.convertToSQL(any(), ModelName.Deepseek, ModelVariant.Baseline)
        } returns ResultWrapper.Success("SELECT * FROM puzzles WHERE rating > 1500")

        // Act
        val result = benchmarkService.getBenchmark(benchmarkEntries)

        // Assert
        val expectedResults =
            listOf(
                BenchmarkResult(
                    text = "Find puzzles with rating > 1500",
                    full = SqlResult(sql = "ERROR", status = "0"),
                    partial =
                        SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 1500", status = ""),
                    baseline =
                        SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 1500", status = ""),
                ),
                BenchmarkResult(
                    text = "Find puzzles with rating > 2000",
                    full = SqlResult(sql = "ERROR", status = "0"),
                    partial =
                        SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 1500", status = ""),
                    baseline =
                        SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 1500", status = ""),
                ),
            )
        expectThat(result).isEqualTo(ResultWrapper.Success(expectedResults))
    }

    @Test
    fun `test getBenchmark failure in Partial model`(): Unit = runBlocking {
        // Arrange
        val benchmarkEntries =
            listOf(
                BenchmarkEntry(text = "Find puzzles with rating > 1500"),
                BenchmarkEntry(text = "Find puzzles with rating > 2000"),
            )

        val error = CallLargeLanguageModelError.RateLimitError

        coEvery {
            text2SQLService.convertToSQL(any(), ModelName.Deepseek, ModelVariant.Full)
        } returns ResultWrapper.Success("SELECT * FROM puzzles WHERE rating > 1500")
        coEvery {
            text2SQLService.convertToSQL(any(), ModelName.Deepseek, ModelVariant.Partial)
        } returns ResultWrapper.Failure(error)
        coEvery {
            text2SQLService.convertToSQL(any(), ModelName.Deepseek, ModelVariant.Baseline)
        } returns ResultWrapper.Success("SELECT * FROM puzzles WHERE rating > 1500")

        // Act
        val result = benchmarkService.getBenchmark(benchmarkEntries)

        // Assert
        val expectedResults =
            listOf(
                BenchmarkResult(
                    text = "Find puzzles with rating > 1500",
                    full =
                        SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 1500", status = ""),
                    partial = SqlResult(sql = "ERROR", status = "0"),
                    baseline =
                        SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 1500", status = ""),
                ),
                BenchmarkResult(
                    text = "Find puzzles with rating > 2000",
                    full =
                        SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 1500", status = ""),
                    partial = SqlResult(sql = "ERROR", status = "0"),
                    baseline =
                        SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 1500", status = ""),
                ),
            )
        expectThat(result).isEqualTo(ResultWrapper.Success(expectedResults))
    }

    @Test
    fun `test getBenchmark failure in Baseline model`(): Unit = runBlocking {
        // Arrange
        val benchmarkEntries =
            listOf(
                BenchmarkEntry(text = "Find puzzles with rating > 1500"),
                BenchmarkEntry(text = "Find puzzles with rating > 2000"),
            )

        val error = GetTextFileError.IOException(IOException())

        coEvery {
            text2SQLService.convertToSQL(any(), ModelName.Deepseek, ModelVariant.Full)
        } returns ResultWrapper.Success("SELECT * FROM puzzles WHERE rating > 1500")
        coEvery {
            text2SQLService.convertToSQL(any(), ModelName.Deepseek, ModelVariant.Partial)
        } returns ResultWrapper.Success("SELECT * FROM puzzles WHERE rating > 1500")
        coEvery {
            text2SQLService.convertToSQL(any(), ModelName.Deepseek, ModelVariant.Baseline)
        } returns ResultWrapper.Failure(error)

        // Act
        val result = benchmarkService.getBenchmark(benchmarkEntries)

        // Assert
        val expectedResults =
            listOf(
                BenchmarkResult(
                    text = "Find puzzles with rating > 1500",
                    full =
                        SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 1500", status = ""),
                    partial =
                        SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 1500", status = ""),
                    baseline = SqlResult(sql = "ERROR", status = "0"),
                ),
                BenchmarkResult(
                    text = "Find puzzles with rating > 2000",
                    full =
                        SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 1500", status = ""),
                    partial =
                        SqlResult(sql = "SELECT * FROM puzzles WHERE rating > 1500", status = ""),
                    baseline = SqlResult(sql = "ERROR", status = "0"),
                ),
            )
        expectThat(result).isEqualTo(ResultWrapper.Success(expectedResults))
    }
}
