package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.entities.Demonstration
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.ProcessPromptError
import com.chess.puzzle.text2sql.web.entities.helper.ProcessPromptError.InsufficientDemonstrationsError
import com.chess.puzzle.text2sql.web.entities.helper.ProcessPromptError.InvalidDemonstrationError
import com.chess.puzzle.text2sql.web.entities.helper.ProcessPromptError.MissingPlaceholderError
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
            val promptTemplateWithUserPrompt = loadPrompt(userPrompt, promptTemplate)
            val processedPromptTemplate =
                if (demonstrations != null) {
                    loadDemonstrations(promptTemplateWithUserPrompt, demonstrations)
                } else {
                    promptTemplateWithUserPrompt
                }
            ResultWrapper.Success(processedPromptTemplate)
        } catch (e: InvalidDemonstrationException) {
            ResultWrapper.Failure(InvalidDemonstrationError)
        } catch (e: InsufficientDemonstrationsException) {
            ResultWrapper.Failure(InsufficientDemonstrationsError)
        } catch (e: MissingPlaceholderException) {
            ResultWrapper.Failure(MissingPlaceholderError)
        }
    }

    private fun loadPrompt(prompt: String, template: String): String {
        val sb = StringBuilder(template)
        val promptPlaceholder = "{{prompt}}"
        if (!sb.contains(promptPlaceholder)) throw MissingPlaceholderException()
        val index = sb.indexOf(promptPlaceholder)
        sb.replace(index, index + promptPlaceholder.length, prompt)
        return sb.toString()
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
        similarDemonstration.forEach {
            if (it.text.isEmpty() || it.sql.isEmpty()) {
                throw InvalidDemonstrationException()
            }
        }
        if (similarDemonstration.size < 3) {
            throw InsufficientDemonstrationsException()
        }

        val textArray: Array<String> = similarDemonstration.map { it.text }.toTypedArray()
        val sqlArray: Array<String> = similarDemonstration.map { it.sql }.toTypedArray()

        val sb = StringBuilder(template)
        for (i in 0 until 3) {
            val textPlaceholder = "{{text$i}}"
            val sqlPlaceholder = "{{sql$i}}"

            if (!sb.contains(textPlaceholder) || !sb.contains(sqlPlaceholder)) {
                throw MissingPlaceholderException()
            }

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

    private class InvalidDemonstrationException : Throwable()

    private class InsufficientDemonstrationsException : Throwable()

    private class MissingPlaceholderException : Throwable()
}
