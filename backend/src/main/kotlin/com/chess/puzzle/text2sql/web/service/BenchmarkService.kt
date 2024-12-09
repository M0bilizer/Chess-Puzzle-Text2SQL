package com.chess.puzzle.text2sql.web.service

import ch.qos.logback.classic.Level
import com.chess.puzzle.text2sql.web.config.CustomTimeout
import com.chess.puzzle.text2sql.web.entities.helper.BenchmarkEntry
import com.chess.puzzle.text2sql.web.entities.helper.BenchmarkResult
import com.chess.puzzle.text2sql.web.entities.helper.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.SqlResult
import com.chess.puzzle.text2sql.web.service.ModelName.Baseline
import com.chess.puzzle.text2sql.web.service.ModelName.Full
import com.chess.puzzle.text2sql.web.service.ModelName.Partial
import com.chess.puzzle.text2sql.web.utility.withLogLevel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.io.File

private val logger = KotlinLogging.logger {}

@Service
class BenchmarkService(
    @Autowired private val text2SQLService: Text2SQLService,
) {
    private val jsonPath = "src/main/resources/data/benchmark.json"
    private val cleanedJson = File(jsonPath).readText().replace(Regex("/\\*(.|\\R)*?\\*/"), "")
    private val benchmarkEntryList: List<BenchmarkEntry> = Json.decodeFromString<List<BenchmarkEntry>>(cleanedJson)

    @Async
    @CustomTimeout(360000)
    suspend fun getBenchmark(): List<BenchmarkResult> {
        val benchmarkResultList = mutableListOf<BenchmarkResult>()
        withLogLevel(Level.OFF) {
            for (benchmarkEntry in benchmarkEntryList.take(3)) {
                val text = benchmarkEntry.text
                benchmarkResultList.add(
                    BenchmarkResult(
                        text = text,
                        full = getResult(text, Full),
                        partial = getResult(text, Partial),
                        baseline = getResult(text, Baseline),
                    ),
                )
            }
        }
        return benchmarkResultList
    }

    private suspend fun getResult(
        text: String,
        modelName: ModelName,
    ): SqlResult {
        println("${modelName.name}: $text")
        return when (modelName) {
            Full -> {
                val result = text2SQLService.convertToSQL(text)
                if (result is ResultWrapper.Success) {
                    SqlResult(sql = result.data, status = "")
                } else {
                    SqlResult(sql = "ERROR", status = "0")
                }
            }
            Partial -> {
                val result = text2SQLService.partialConvertToSQL(text)
                if (result is ResultWrapper.Success) {
                    SqlResult(sql = result.data, status = "")
                } else {
                    SqlResult(sql = "ERROR", status = "0")
                }
            }
            Baseline -> {
                val result = text2SQLService.baselineConvertToSQL(text)
                if (result is ResultWrapper.Success) {
                    SqlResult(sql = result.data, status = "")
                } else {
                    SqlResult(sql = "ERROR", status = "0")
                }
            }
        }
    }
}

private enum class ModelName { Full, Partial, Baseline }
