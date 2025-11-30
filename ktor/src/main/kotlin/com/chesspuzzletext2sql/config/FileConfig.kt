package com.chesspuzzletext2sql.config

import com.charleskorn.kaml.Yaml
import com.chesspuzzletext2sql.features.prompts.models.PromptTemplate
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

@Serializable
data class FilesConfig(val default: String, val templates: Map<String, PromptTemplate>)

object FileConfigLoader {
    private val yaml = Yaml.default

    fun loadTemplates(): Result<FilesConfig, Throwable> = runCatching {
        val configFile = File("prompt-templates.yaml").readText()
        yaml.decodeFromString<FilesConfig>(configFile)
    }
}
