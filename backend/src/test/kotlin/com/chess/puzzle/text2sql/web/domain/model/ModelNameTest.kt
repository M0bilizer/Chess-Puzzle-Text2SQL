package com.chess.puzzle.text2sql.web.domain.model

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ModelNameTest {

    @Test
    fun `toEnum should convert valid strings to ModelName`() {
        expectThat(ModelName.toEnum("Full")).isEqualTo(ModelName.Full)
        expectThat(ModelName.toEnum("full")).isEqualTo(ModelName.Full)
        expectThat(ModelName.toEnum("fUlL")).isEqualTo(ModelName.Full)
        expectThat(ModelName.toEnum("Partial")).isEqualTo(ModelName.Partial)
        expectThat(ModelName.toEnum("Baseline")).isEqualTo(ModelName.Baseline)
    }

    @Test
    fun `toEnum should fallback to Full for invalid or null strings`() {
        expectThat(ModelName.toEnum("Unknown")).isEqualTo(null)
        expectThat(ModelName.toEnum("")).isEqualTo(null)
    }
}
