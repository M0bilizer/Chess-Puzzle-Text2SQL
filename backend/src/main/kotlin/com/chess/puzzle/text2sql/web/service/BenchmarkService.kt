package com.chess.puzzle.text2sql.web.service

import ch.qos.logback.classic.Level
import com.chess.puzzle.text2sql.web.entities.helper.BenchmarkEntry
import com.chess.puzzle.text2sql.web.entities.helper.BenchmarkResult
import com.chess.puzzle.text2sql.web.entities.helper.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.SqlResult
import com.chess.puzzle.text2sql.web.service.ModelName.Baseline
import com.chess.puzzle.text2sql.web.service.ModelName.Full
import com.chess.puzzle.text2sql.web.service.ModelName.Partial
import com.chess.puzzle.text2sql.web.utility.withLogLevel
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * Service for benchmarking the Text2SQL model.
 *
 * ### Text2SQL Models and Ablation Test
 * In this web application, the Text2SQL process is supported by two key functions:
 * 1. **Finding Similar Demonstrations**: Identifying similar examples to improve the model's
 *    understanding of the query.
 * 2. **Schema Masking**: Masking database-specific keywords to improve the model's generalization.
 *
 * To evaluate the effectiveness of these functions, an ablation test was performed using three
 * models:
 * - **Full Model**: Combines both finding similar demonstrations and schema masking.
 * - **Partial Model**: Includes finding similar demonstrations but excludes schema masking.
 * - **Baseline Model**: Represents the simplest model without any additional functions.
 *
 * ### Responsibilities
 * The service is responsible for:
 * - Loading benchmark entries from a JSON file.
 * - Evaluating the performance of the models against the benchmark entries.
 * - Generating a list of [BenchmarkResult] objects containing the results.
 *
 * ### Benchmark Process
 * The benchmarking process involves the following steps:
 * 1. Load benchmark entries from the `benchmark.json` file.
 * 2. For each benchmark entry, evaluate the text against the three models:
 *     - **Full Model**: Uses both similar demonstrations and schema masking.
 *     - **Partial Model**: Uses similar demonstrations but no schema masking.
 *     - **Baseline Model**: Uses no additional functions.
 * 3. Record the results for each model in a [BenchmarkResult] object.
 * 4. Return a list of [BenchmarkResult] objects containing the benchmark results.
 *
 * @property text2SQLService The service used to convert text to SQL for benchmarking.
 */
@Service
class BenchmarkService(@Autowired private val text2SQLService: Text2SQLService) {
    private val jsonPath = "src/main/resources/data/benchmark.json"
    private val cleanedJson = File(jsonPath).readText().replace(Regex("/\\*(.|\\R)*?\\*/"), "")
    private val benchmarkEntryList: List<BenchmarkEntry> =
        Json.decodeFromString<List<BenchmarkEntry>>(cleanedJson)

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
    suspend fun getBenchmark(): List<BenchmarkResult> {
        val benchmarkResultList = mutableListOf<BenchmarkResult>()
        logger.info { "Starting Benchmark" }

        for ((index, benchmarkEntry) in benchmarkEntryList.withIndex()) {
            val text = benchmarkEntry.text
            logger.info { "Testing $index: $text" }
            val (full, partial, baseline) = evaluateModels(text)
            benchmarkResultList.add(BenchmarkResult(text, full, partial, baseline))
        }

        logger.info { "Benchmark Complete" }
        return benchmarkResultList
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
    private suspend fun evaluateModels(text: String): Triple<SqlResult, SqlResult, SqlResult> {
        val full = convertTextToSQL(text, Full)
        val partial = convertTextToSQL(text, Partial)
        val baseline = convertTextToSQL(text, Baseline)
        return Triple(full, partial, baseline)
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
        val result =
            withLogLevel(Level.OFF) {
                return@withLogLevel when (modelName) {
                    Full -> text2SQLService.convertToSQL(text)
                    Partial -> text2SQLService.partialConvertToSQL(text)
                    Baseline -> text2SQLService.baselineConvertToSQL(text)
                }
            }
        return when (result) {
            is ResultWrapper.Success -> {
                logger.info { "  $modelName - ok!" }
                SqlResult(sql = result.data, status = "")
            }
            is ResultWrapper.Error -> {
                logger.info { "  $modelName - ERROR: " }
                SqlResult(sql = "ERROR", status = "0")
            }
        }
    }
}

/**
 * Enum class representing the different models used for benchmarking.
 * - [Full]: The full model with two features: finding similar demonstrations and schema masking.
 * - [Partial]: The partial model with one feature: finding similar demonstrations.
 * - [Baseline]: The baseline model with no additional feature.
 */
private enum class ModelName {
    Full,
    Partial,
    Baseline,
}
