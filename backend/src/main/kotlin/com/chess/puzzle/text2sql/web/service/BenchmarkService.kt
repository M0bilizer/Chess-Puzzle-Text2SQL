package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.entities.helper.BenchmarkData
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import java.io.File

@Service
class BenchmarkService {
    private val jsonPath = "src/main/resources/data/benchmark.json"
    private val cleanedJson = File(jsonPath).readText().replace(Regex("/\\*(.|\\R)*?\\*/"), "")
    private val jsonObject: BenchmarkData = Json.decodeFromString<BenchmarkData>(cleanedJson)

    fun getBenchmark(): BenchmarkData {
        return jsonObject
    }
}
