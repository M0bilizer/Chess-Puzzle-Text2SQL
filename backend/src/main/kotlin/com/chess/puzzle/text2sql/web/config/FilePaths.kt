package com.chess.puzzle.text2sql.web.config

import com.chess.puzzle.text2sql.web.domain.model.ModelVariant
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * A configuration class that holds file paths used in the application.
 *
 * This class is annotated with `@Component` to be automatically detected by Spring's component
 * scanning. It uses `@Value` annotations to inject property values from the application's
 * configuration files.
 *
 * @property jsonPath The file path to the benchmark JSON file.
 * @property promptTemplateMdPath The file path to the prompt template markdown file.
 * @property baselinePromptTemplateMdPath The file path to the baseline prompt template markdown
 *   file.
 */
@Component
data class FilePaths(
    @Value("\${benchmark.json.path}") val jsonPath: String,
    @Value("\${promptTemplate.md.path}") val promptTemplateMdPath: String,
    @Value("\${baselinePromptTemplate.md.path}") val baselinePromptTemplateMdPath: String,
) {
    /**
     * Retrieves the appropriate prompt template file path based on the specified model name.
     *
     * This method returns the file path to the prompt template that corresponds to the given model
     * name. If the model name is `Full` or `Partial`, it returns the `promptTemplateMdPath`. If the
     * model name is `Baseline`, it returns the `baselinePromptTemplateMdPath`.
     *
     * @param modelVariant The name of the model for which to retrieve the prompt template.
     * @return The file path to the appropriate prompt template.
     */
    fun getPromptTemplate(modelVariant: ModelVariant): String {
        return when (modelVariant) {
            ModelVariant.Full -> promptTemplateMdPath
            ModelVariant.Partial -> promptTemplateMdPath
            ModelVariant.Baseline -> baselinePromptTemplateMdPath
        }
    }
}
