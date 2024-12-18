package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.entities.Demonstration
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.ProcessPromptError
import com.chess.puzzle.text2sql.web.entities.helper.ProcessPromptError.UnexpectedError
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class PreprocessingHelper {

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
        promptTemplate: String,
        demonstrations: List<Demonstration>?,
    ): ResultWrapper<String, ProcessPromptError> {
        return try {
            val promptTemplateWithUserPrompt = promptTemplate.replace("{{prompt}}", userPrompt)
            val processedPromptTemplate =
                if (demonstrations != null) {
                    loadDemonstrations(promptTemplateWithUserPrompt, demonstrations)
                } else {
                    promptTemplateWithUserPrompt
                }
            ResultWrapper.Success(processedPromptTemplate)
        } catch (e: java.io.IOException) {
            logger.error { "Failed to load prompt template: ${e.message}" }
            ResultWrapper.Failure(ProcessPromptError.IOException(e))
        } catch (e: Exception) {
            logger.error { "Unexpected error while processing prompt: ${e.message}" }
            ResultWrapper.Failure(UnexpectedError(e))
        }
    }

    /**
     * Injects similar demonstrations into the prompt template.
     *
     * This method replaces placeholders in the template (e.g., `{{text0}}`, `{{sql0}}`) with the
     * corresponding text and SQL from the similar demonstrations.
     *
     * @param template The prompt template with placeholders.
     * @param similarDemonstration A list of similar demonstrations to be injected.
     * @return The prompt template with demonstrations injected.
     */
    private fun loadDemonstrations(
        template: String,
        similarDemonstration: List<Demonstration>,
    ): String {
        val textArray: Array<String> = similarDemonstration.map { it.text }.toTypedArray()
        val sqlArray: Array<String> = similarDemonstration.map { it.sql }.toTypedArray()

        val sb = StringBuilder(template)
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
