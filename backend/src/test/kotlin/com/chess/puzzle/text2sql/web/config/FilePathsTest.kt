package com.chess.puzzle.text2sql.web.config

import com.chess.puzzle.text2sql.web.domain.model.ModelVariant
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
@TestPropertySource(
    properties =
        [
            "benchmark.json.path=path/to/benchmark.json",
            "promptTemplate.md.path=path/to/promptTemplate.md",
            "baselinePromptTemplate.md.path=path/to/baselinePromptTemplate.md",
        ]
)
class FilePathsTest {

    @Autowired private lateinit var filePaths: FilePaths

    @Test
    fun `test property injection`() {
        // Assert
        expectThat(filePaths.jsonPath) { isEqualTo("path/to/benchmark.json") }
        expectThat(filePaths.promptTemplateMdPath) { isEqualTo("path/to/promptTemplate.md") }
        expectThat(filePaths.baselinePromptTemplateMdPath) {
            isEqualTo("path/to/baselinePromptTemplate.md")
        }
    }

    @Test
    fun `test getPromptTemplate with Full model`() {
        // Act
        val result = filePaths.getPromptTemplate(ModelVariant.Full)

        // Assert
        expectThat(result) { isEqualTo("path/to/promptTemplate.md") }
    }

    @Test
    fun `test getPromptTemplate with Partial model`() {
        // Act
        val result = filePaths.getPromptTemplate(ModelVariant.Partial)

        // Assert
        expectThat(result) { isEqualTo("path/to/promptTemplate.md") }
    }

    @Test
    fun `test getPromptTemplate with Baseline model`() {
        // Act
        val result = filePaths.getPromptTemplate(ModelVariant.Baseline)

        // Assert
        expectThat(result) { isEqualTo("path/to/baselinePromptTemplate.md") }
    }
}
