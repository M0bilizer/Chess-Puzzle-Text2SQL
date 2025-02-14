package com.chess.puzzle.text2sql.web.domain.model

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ModelVariantTest {

    @Test
    fun `toEnum should convert valid strings to ModelName`() {
        expectThat(ModelVariant.toEnum("Full")).isEqualTo(ModelVariant.Full)
        expectThat(ModelVariant.toEnum("full")).isEqualTo(ModelVariant.Full)
        expectThat(ModelVariant.toEnum("fUlL")).isEqualTo(ModelVariant.Full)
        expectThat(ModelVariant.toEnum("Partial")).isEqualTo(ModelVariant.Partial)
        expectThat(ModelVariant.toEnum("Baseline")).isEqualTo(ModelVariant.Baseline)
    }

    @Test
    fun `toEnum should fallback to Full for invalid or null strings`() {
        expectThat(ModelVariant.toEnum("Unknown")).isEqualTo(null)
        expectThat(ModelVariant.toEnum("")).isEqualTo(null)
    }
}
