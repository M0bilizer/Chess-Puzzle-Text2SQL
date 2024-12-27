package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.config.FilePaths
import com.chess.puzzle.text2sql.web.entities.BenchmarkEntry
import com.chess.puzzle.text2sql.web.entities.BenchmarkResult
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.service.BenchmarkService
import com.chess.puzzle.text2sql.web.service.FileLoaderService
import com.chess.puzzle.text2sql.web.service.JsonWriterService
import com.chess.puzzle.text2sql.web.utility.ResponseUtils.failure
import com.chess.puzzle.text2sql.web.utility.ResponseUtils.success
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.async.DeferredResult

private val logger = KotlinLogging.logger {}

/**
 * REST Controller to handle benchmarking requests for the Text2SQL application.
 *
 * This controller provides endpoints for:
 * - Running performance benchmarks and saving the results to a JSON file.
 *
 * The controller uses the following services:
 * - [BenchmarkService]: Runs the benchmark process.
 * - [JsonWriterService]: Writes the benchmark results to a JSON file.
 * - [FileLoaderService]: Loads benchmark entries from a file.
 * - [FilePaths]: Provides file paths for benchmark data and results.
 */
@RestController
class BenchmarkingController(
    @Autowired private val benchmarkService: BenchmarkService,
    @Autowired private val jsonWriterService: JsonWriterService,
    @Autowired private val fileLoaderService: FileLoaderService,
    @Autowired private val filePaths: FilePaths,
) {

    /**
     * Benchmarking endpoint to start the benchmark process.
     *
     * This endpoint:
     * 1. Loads benchmark entries from a file using [FileLoaderService].
     * 2. Runs the benchmark process using [BenchmarkService].
     * 3. Saves the benchmark results to a JSON file using [JsonWriterService].
     *
     * @return A [DeferredResult] containing the benchmark results if successful, or an error
     *   message if the process fails.
     */
    @GetMapping("/api/benchmark")
    suspend fun benchmark(): DeferredResult<ResponseEntity<String>> {
        logger.info { "Received GET on /api/benchmark" }
        val deferredResult = DeferredResult<ResponseEntity<String>>(1350000)

        runBlocking {
            val benchmark: List<BenchmarkResult>
            val benchmarkEntries: List<BenchmarkEntry>
            val benchmarkEntriesFilePath = filePaths.jsonPath
            when (val result = fileLoaderService.getBenchmarkEntries(benchmarkEntriesFilePath)) {
                is ResultWrapper.Success -> benchmarkEntries = result.data
                is ResultWrapper.Failure -> {
                    deferredResult.setResult(failure(result.error))
                    return@runBlocking
                }
            }
            when (val result = benchmarkService.getBenchmark(benchmarkEntries)) {
                is ResultWrapper.Success -> benchmark = result.data
                is ResultWrapper.Failure -> {
                    deferredResult.setResult(failure(result.error))
                    return@runBlocking
                }
            }
            val jsonString = Json.encodeToString(benchmark)
            val filePath = "src/main/resources/data/benchmarkResult.json"
            when (val result = jsonWriterService.writeToFile(filePath, jsonString)) {
                is ResultWrapper.Success -> deferredResult.setResult(success(benchmark))
                is ResultWrapper.Failure -> deferredResult.setResult(failure(result.error))
            }
        }

        return deferredResult
    }
}
