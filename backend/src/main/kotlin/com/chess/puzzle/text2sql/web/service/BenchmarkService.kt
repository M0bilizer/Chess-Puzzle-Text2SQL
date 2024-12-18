package com.chess.puzzle.text2sql.web.service

import ch.qos.logback.classic.Level
import com.chess.puzzle.text2sql.web.entities.BenchmarkEntry
import com.chess.puzzle.text2sql.web.entities.BenchmarkResult
import com.chess.puzzle.text2sql.web.entities.ModelName
import com.chess.puzzle.text2sql.web.entities.ModelName.*
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.CustomError
import com.chess.puzzle.text2sql.web.entities.helper.SqlResult
import com.chess.puzzle.text2sql.web.utility.withLogLevel
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class BenchmarkService(@Autowired private val text2SQLService: Text2SQLService) {

    /**
     * Runs the benchmark for all entries and returns the results.
     *
     * This method iterates over the list of benchmark entries and evaluates each one against the
     * three models ([Full], [Partial], [Baseline]). The results are stored in a list of
     * [BenchmarkResult] objects.
     *
     * @return A list of [BenchmarkResult] objects containing the benchmark results.
     * @see [BenchmarkResult]
     */
    suspend fun getBenchmark(
        benchmarkEntries: List<BenchmarkEntry>
    ): ResultWrapper<List<BenchmarkResult>, CustomError> {
        val benchmarkResultList = mutableListOf<BenchmarkResult>()
        logger.info { "== Starting Benchmark ==" }

        for ((index, benchmarkEntry) in benchmarkEntries.withIndex()) {
            val text = benchmarkEntry.text
            logger.info { "Testing $index: $text" }
            val (full, partial, baseline) = evaluateModels(text)
            benchmarkResultList.add(BenchmarkResult(text, full, partial, baseline))
        }

        logger.info { "== Benchmark Complete ==" }
        return ResultWrapper.Success(benchmarkResultList)
    }

    /**
     * Evaluates a single benchmark entry against the three models ([Full], [Partial], [Baseline]).
     *
     * This method converts the given text to SQL using each model and returns the results as a
     * [Triple] containing three [SqlResult] objects. Each [SqlResult] represents the result of the
     * conversion for a specific model.
     *
     * @param text The text to convert to SQL.
     * @return A [Triple] containing the [SqlResult] for [Full], [Partial], and [Baseline] models.
     */
    suspend fun evaluateModels(text: String): Triple<SqlResult, SqlResult, SqlResult> {
        return Triple(
            convertTextToSQL(text, Full),
            convertTextToSQL(text, Partial),
            convertTextToSQL(text, Baseline),
        )
    }

    /**
     * Converts the given text to SQL using the specified model.
     *
     * This method uses the provided model ([Full], [Partial], or [Baseline]) to convert the text
     * into SQL. The status field of the [SqlResult] will be changed manually by hand. If the
     * conversion is successful, the result contains the generated SQL; otherwise, it contains an
     * 'ERROR' for the 'sql' field.
     *
     * @param text The text to convert to SQL.
     * @param modelName The model to use for the conversion ([Full], [Partial], [Baseline]).
     * @return An [SqlResult] object containing the SQL result or an 'ERROR' SQL.
     */
    private suspend fun convertTextToSQL(text: String, modelName: ModelName): SqlResult {
        val result: ResultWrapper<String, CustomError> =
            withLogLevel(Level.OFF) {
                return@withLogLevel text2SQLService.convertToSQL(text, modelName)
            }
        return when (result) {
            is ResultWrapper.Success -> {
                logger.info { "  $modelName - ok!" }
                SqlResult(sql = result.data, status = "")
            }
            is ResultWrapper.Failure -> {
                logger.info { "  $modelName - Error: ${result.error.message}" }
                SqlResult(sql = "ERROR", status = "0")
            }
        }
    }
}
