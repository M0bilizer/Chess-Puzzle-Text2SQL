package com.chess.puzzle.text2sql.web.domain.input

import com.chess.puzzle.text2sql.web.domain.model.ModelVariant
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.ClientError
import com.chess.puzzle.text2sql.web.error.ClientError.InvalidModelVariant
import com.chess.puzzle.text2sql.web.error.ClientError.MissingQuery
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class Text2SqlRequestTest {

    @Test
    fun `toInput should return Success when query and model are valid`() {
        val query = "My Query"
        val request = Text2SqlRequest(query, "Full")
        val result = request.toInput()

        val expected = Text2SqlInput(query, ModelVariant.Full)
        expectThat(result).isEqualTo(ResultWrapper.Success(expected))
    }

    @Test
    fun `toInput should return Success when query and partial model are valid`() {
        val query = "My Query"
        val request = Text2SqlRequest(query, "Partial")
        val result = request.toInput()

        val expected = Text2SqlInput(query, ModelVariant.Partial)
        expectThat(result).isEqualTo(ResultWrapper.Success(expected))
    }

    @Test
    fun `toInput should return Success when query and baseline model are valid`() {
        val query = "My Query"
        val request = Text2SqlRequest(query, "Baseline")
        val result = request.toInput()

        val expected = Text2SqlInput(query, ModelVariant.Baseline)
        expectThat(result).isEqualTo(ResultWrapper.Success(expected))
    }

    @Test
    fun `toInput should return Success when query and uppercased model are valid`() {
        val query = "My Query"
        val request = Text2SqlRequest(query, "FULL")
        val result = request.toInput()

        val expected = Text2SqlInput(query, ModelVariant.Full)
        expectThat(result).isEqualTo(ResultWrapper.Success(expected))
    }

    @Test
    fun `toInput should return Success when query and undercased model are valid`() {
        val query = "My Query"
        val request = Text2SqlRequest(query, "partial")
        val result = request.toInput()

        val expected = Text2SqlInput(query, ModelVariant.Partial)
        expectThat(result).isEqualTo(ResultWrapper.Success(expected))
    }

    @Test
    fun `toInput should return Success when query and random-cased model are valid`() {
        val query = "My Query"
        val request = Text2SqlRequest(query, "bAseLInE")
        val result = request.toInput()

        val expected = Text2SqlInput(query, ModelVariant.Baseline)
        expectThat(result).isEqualTo(ResultWrapper.Success(expected))
    }

    @Test
    fun `toInput should return Failure with MissingQuery when query is null`() {
        val request = Text2SqlRequest(query = null, model = "Full")
        val result = request.toInput()

        val expected = listOf(MissingQuery)
        expectThat(result).isEqualTo(ResultWrapper.Failure(expected))
    }

    @Test
    fun `toInput should return Failure with InvalidModelName when model is invalid`() {
        val request = Text2SqlRequest(query = "SELECT * FROM users", model = "InvalidModel")
        val result = request.toInput()

        val expected = listOf(InvalidModelVariant)
        expectThat(result).isEqualTo(ResultWrapper.Failure(expected))
    }

    @Test
    fun `toInput should return Success with default model when model is null`() {
        val query = "My Query"
        val request = Text2SqlRequest(query)
        val result = request.toInput()

        val expected = Text2SqlInput(query, ModelVariant.Full)
        expectThat(result).isEqualTo(ResultWrapper.Success(expected))
    }

    @Test
    fun `toInput should return Failure with both MissingQuery and InvalidModelName when query is null and model is invalid`() {
        val request = Text2SqlRequest(query = null, model = "InvalidModel")
        val result = request.toInput()

        val expected = listOf(InvalidModelVariant, MissingQuery)
        expectThat(result).isA<ResultWrapper.Failure<List<ClientError>>>().and {
            get { error }.contains(expected)
        }
    }
}
