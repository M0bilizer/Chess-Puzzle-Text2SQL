package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.config.FilePaths
import com.chess.puzzle.text2sql.web.entities.BenchmarkEntry
import com.chess.puzzle.text2sql.web.entities.BenchmarkResult
import com.chess.puzzle.text2sql.web.entities.helper.SqlResult
import com.chess.puzzle.text2sql.web.service.BenchmarkService
import com.chess.puzzle.text2sql.web.service.FileLoaderService
import com.chess.puzzle.text2sql.web.service.JsonWriterService
import com.chess.puzzle.text2sql.web.service.Text2SQLService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.mockk
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BenchmarkingControllerIntegrationTest {
    private val objectMapper = jacksonObjectMapper()

    private val text2SQLService: Text2SQLService = mockk()

    private val benchmarkService: BenchmarkService = BenchmarkService(text2SQLService)
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
}
