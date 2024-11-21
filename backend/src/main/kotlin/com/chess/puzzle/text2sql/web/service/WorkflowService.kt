package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.helper.Demonstration
import com.chess.puzzle.text2sql.web.helper.ResultWrapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.sqrt

private val logger = KotlinLogging.logger {}

@Service
class WorkflowService {
    private val objectMapper = jacksonObjectMapper()
    private val jsonFilePath = "src/main/resources/data/demonstrations.json"
    private val layoutPath = "src/main/resources/prompt/inferencePromptTemplate.md"

    fun processPrompt(userPrompt: String): ResultWrapper<out String> {
        return try {
            val processedLayout = userPrompt.loadLayout()
            val similarDemonstration = findMostSimilarDemonstrations(userPrompt, this.getDemonstration())
            val processedPrompt = processedLayout.loadDemonstrations(similarDemonstration)
            ResultWrapper.Success(processedPrompt)
        } catch (e: Exception) {
            logger.error { "Processing Prompt {userPrompt = $userPrompt} -> ${e.message}" }
            ResultWrapper.Error.ResponseError
        }
    }

    private fun String.loadLayout(): String {
        return try {
            val layoutContent = Files.readString(Paths.get(layoutPath))
            layoutContent.replace("{{prompt}}", this)
        } catch (e: Exception) {
            logger.error { "Loading Layout {} -> Cannot find inferencePromptTemplate.md" }
            throw e
        }
    }

    private fun String.loadDemonstrations(similarDemonstration: List<Demonstration>): String {
        println(similarDemonstration)
        val textArray: Array<String> = similarDemonstration.map { it.text }.toTypedArray()
        val sqlArray: Array<String> = similarDemonstration.map { it.sql }.toTypedArray()

        val sb = StringBuilder(this)
        for (i in 0 until 3) {
            val textPlaceholder = "{{text$i}}"
            val sqlPlaceholder = "{{sql$i}}"
            var index = sb.indexOf(textPlaceholder)
            if (index != -1) {
                sb.replace(index, index + textPlaceholder.length, textArray[i])
            }
            index = sb.indexOf(sqlPlaceholder)
            if (index != -1) {
                sb.replace(index, index + sqlPlaceholder.length, sqlArray[i])
            }
        }
        return sb.toString()
    }

    private fun getDemonstration(): List<Demonstration> {
        return try {
            val file = File(jsonFilePath)
            val jsonContent: Map<String, List<Demonstration>> = objectMapper.readValue(file)

            val combinedDemonstration = mutableListOf<Demonstration>()
            combinedDemonstration.apply {
                addAll(jsonContent["openai"] ?: emptyList())
                addAll(jsonContent["gemini"] ?: emptyList())
                addAll(jsonContent["llama"] ?: emptyList())
            }
        } catch (e: Exception) {
            logger.error { "Getting Demonstration {} -> Cannot find demonstrations.json" }
            throw e
        }
    }

    private fun findMostSimilarDemonstrations(
        userPrompt: String,
        demonstrations: List<Demonstration>,
    ): List<Demonstration> {
        val userVector = this.textToVector(userPrompt)
        val similarities =
            demonstrations.map { demo ->
                val demoVector = this.textToVector(demo.text)
                val similarity = this.cosineSimilarity(userVector, demoVector)
                demo to similarity
            }
        return similarities.sortedByDescending { it.second }.take(3).map { it.first }
    }

    private fun textToVector(text: String): DoubleArray {
        return text.split(" ").map { it.length.toDouble() }.toDoubleArray()
    }

    private fun cosineSimilarity(
        vecA: DoubleArray,
        vecB: DoubleArray,
    ): Double {
        val dotProduct = vecA.zip(vecB) { a, b -> a * b }.sum()
        val normA = sqrt(vecA.sumOf { it * it })
        val normB = sqrt(vecB.sumOf { it * it })
        return if (normA == 0.0 || normB == 0.0) 0.0 else dotProduct / (normA * normB)
    }
}
