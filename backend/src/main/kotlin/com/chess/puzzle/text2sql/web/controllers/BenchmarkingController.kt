package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.service.BenchmarkService
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
 */
@RestController
class BenchmarkingController(
    @Autowired private val benchmarkService: BenchmarkService,
    @Autowired private val jsonWriterService: JsonWriterService,
) {

    /**
     * Benchmarking endpoint to start the benchmark process.
     *
     * Runs the benchmark process and saves the results to a JSON file.
     *
     * @return A [DeferredResult] containing the benchmark results if successful, or an error
     *   message if the process fails.
     */
    @GetMapping("/api/benchmark")
    suspend fun benchmark(): DeferredResult<ResponseEntity<String>> {
        val deferredResult = DeferredResult<ResponseEntity<String>>(1350000)

        runBlocking {
            val benchmark = benchmarkService.getBenchmark()
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
