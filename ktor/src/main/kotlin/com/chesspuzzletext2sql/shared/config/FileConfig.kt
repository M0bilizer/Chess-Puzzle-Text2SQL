package com.chesspuzzletext2sql.shared.config

import com.charleskorn.kaml.Yaml
import com.chesspuzzletext2sql.features.puzzles.domains.PromptTemplate
import com.chesspuzzletext2sql.shared.errors.StartupError
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import java.io.File
import kotlinx.io.files.FileNotFoundException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString

@Serializable
data class FilesConfig(val default: String, val templates: Map<String, PromptTemplate>)

object FileConfigLoader {
    private val yaml = Yaml.default
    private const val PATH = "prompt-templates.yaml"

    fun loadTemplates(): Result<FilesConfig, List<StartupError>> =
        runCatching {
                val configFile = File(PATH).readText()
                yaml.decodeFromString<FilesConfig>(configFile)
            }
            .mapError { e ->
                when (e) {
                    is FileNotFoundException -> listOf(StartupError("$PATH cannot be found"))
                    is SerializationException,
                    is IllegalArgumentException -> listOf(StartupError("$PATH is invalid"))

                    else -> listOf(StartupError("$PATH cannot be read"))
                }
            }
}
