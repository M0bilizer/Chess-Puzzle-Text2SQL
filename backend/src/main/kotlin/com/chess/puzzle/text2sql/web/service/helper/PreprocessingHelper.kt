package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.domain.model.Demonstration
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.ProcessPromptError
import com.chess.puzzle.text2sql.web.error.ProcessPromptError.InsufficientDemonstrationsError
import com.chess.puzzle.text2sql.web.error.ProcessPromptError.InvalidDemonstrationError
import com.chess.puzzle.text2sql.web.error.ProcessPromptError.MissingPlaceholderError
import org.springframework.stereotype.Service

/**
 * Service class for preprocessing user prompts and demonstrations into a formatted prompt template.
 *
 * This class handles:
 * - Loading the user prompt into a template.
 * - Injecting similar demonstrations into the template.
 * - Validating the demonstrations and placeholders.
 */
@Service
class PreprocessingHelper {

    /**
     * Processes the user prompt and similar demonstrations into a prompt template.
     *
     * This method:
     * 1. Loads the user prompt into the template by replacing the `{{prompt}}` placeholder.
     * 2. Injects similar demonstrations into the template by replacing placeholders like
     *    `{{text0}}` and `{{sql0}}`.
     * 3. Validates the demonstrations and placeholders.
     *
     * @param userPrompt The user's input query.
     * @param promptTemplate The template containing placeholders for the prompt and demonstrations.
     * @param demonstrations A list of similar demonstrations to be injected into the template.
     * @return A [ResultWrapper] containing the processed prompt template or an error.
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

    /**
     * Loads the user prompt into the template by replacing the `{{prompt}}` placeholder.
     *
     * @param prompt The user's input query.
     * @param template The template containing the `{{prompt}}` placeholder.
     * @return The template with the user prompt injected.
     * @throws MissingPlaceholderException If the `{{prompt}}` placeholder is not found in the
     *   template.
     */
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
     * @throws InvalidDemonstrationException If any demonstration has empty text or SQL.
     * @throws InsufficientDemonstrationsException If fewer than 3 demonstrations are provided.
     * @throws MissingPlaceholderException If any required placeholder is missing in the template.
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

    /** Exception thrown when a demonstration is invalid (e.g., empty text or SQL). */
    private class InvalidDemonstrationException : Throwable()

    /** Exception thrown when fewer than 3 demonstrations are provided. */
    private class InsufficientDemonstrationsException : Throwable()

    /** Exception thrown when a required placeholder is missing in the template. */
    private class MissingPlaceholderException : Throwable()
}
