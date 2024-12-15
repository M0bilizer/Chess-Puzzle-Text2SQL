package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.entities.Demonstration
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.ProcessPromptError
import com.chess.puzzle.text2sql.web.entities.helper.ProcessPromptError.CannotFindLayout
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * A helper service for preprocessing user prompts and similar demonstrations into a prompt
 * template.
 *
 * This class is responsible for:
 * - Loading a prompt template from a file.
 * - Injecting user queries and similar demonstrations into the template.
 * - Providing methods for processing prompts with or without demonstrations.
 */
@Service
class PreprocessingHelper {
    private val layoutPath = "src/main/resources/prompt/inferencePromptTemplate.md"
    private val baselineLayoutPath = "src/main/resources/prompt/inferencePromptTemplate-baseline.md"

    /**
     * Processes the user prompt and similar demonstrations into a prompt template.
     *
     * This method:
     * 1. Loads the prompt template from a file.
     * 2. Replaces placeholders in the template with the user prompt.
     * 3. Injects similar demonstrations into the template.
     *
     * @param userPrompt The user's input query.
     * @param demonstrations A list of similar demonstrations to be injected into the template.
     * @return The processed prompt template with the user query and demonstrations.
     */
    fun processPrompt(
        userPrompt: String,
        demonstrations: List<Demonstration>,
    ): ResultWrapper<String, ProcessPromptError> {
        return try {
            val processedLayout = userPrompt.loadLayout().loadDemonstrations(demonstrations)
            ResultWrapper.Success(processedLayout)
        } catch (e: IOException) {
            ResultWrapper.Failure(CannotFindLayout)
        }
    }

    /**
     * Loads the prompt template from a file and replaces the `{{prompt}}` placeholder with the user
     * prompt.
     *
     * @return The prompt template with the user prompt injected.
     * @receiver The user's input query.
     * @throws Exception If the prompt template file cannot be found.
     */
    private fun String.loadLayout(): String {
        return try {
            val layoutContent = Files.readString(Paths.get(layoutPath))
            layoutContent.replace("{{prompt}}", this)
        } catch (e: IOException) {
            logger.error { "Loading Layout {} -> Cannot find inferencePromptTemplate.md" }
            throw e
        }
    }

    /**
     * Injects similar demonstrations into the prompt template.
     *
     * This method replaces placeholders in the template (e.g., `{{text0}}`, `{{sql0}}`) with the
     * corresponding text and SQL from the similar demonstrations.
     *
     * @param similarDemonstration A list of similar demonstrations to be injected.
     * @return The prompt template with demonstrations injected.
     * @receiver The prompt template with placeholders.
     */
    private fun String.loadDemonstrations(similarDemonstration: List<Demonstration>): String {
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

    /**
     * Processes the user prompt into a prompt template without injecting demonstrations.
     *
     * This method is used for benchmarking purposes and loads a baseline prompt template with the
     * user query.
     *
     * @param userPrompt The user's input query.
     * @return The processed baseline prompt template with the user query.
     */
    fun processBaselinePrompt(userPrompt: String): ResultWrapper<String, ProcessPromptError> {
        return try {
            val processedLayout = userPrompt.loadBaselineLayout()
            ResultWrapper.Success(processedLayout)
        } catch (e: IOException) {
            ResultWrapper.Failure(CannotFindLayout)
        }
    }

    /**
     * Loads the baseline prompt template from a file and replaces the `{{prompt}}` placeholder with
     * the user prompt.
     *
     * @return The baseline prompt template with the user prompt injected.
     * @receiver The user's input query.
     * @throws Exception If the baseline prompt template file cannot be found.
     */
    private fun String.loadBaselineLayout(): String {
        return try {
            val baselineLayoutContent = Files.readString(Paths.get(baselineLayoutPath))
            baselineLayoutContent.replace("{{prompt}}", this)
        } catch (e: IOException) {
            logger.error {
                "Loading Baseline Layout {} -> Cannot find inferencePromptTemplate-baseline.md"
            }
            throw e
        }
    }
}
