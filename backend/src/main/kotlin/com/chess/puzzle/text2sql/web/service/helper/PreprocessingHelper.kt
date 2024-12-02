package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.entities.helper.Demonstration
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths

private val logger = KotlinLogging.logger {}

@Service
class PreprocessingHelper {
    private val layoutPath = "src/main/resources/prompt/inferencePromptTemplate.md"

    fun processPrompt(
        userPrompt: String,
        demonstrations: List<Demonstration>,
    ): String {
        val processedLayout = userPrompt.loadLayout()
        return processedLayout.loadDemonstrations(demonstrations)
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
}
