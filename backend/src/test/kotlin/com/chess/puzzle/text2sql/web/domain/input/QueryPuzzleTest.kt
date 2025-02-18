package com.chess.puzzle.text2sql.web.domain.input

import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.ClientError
import com.chess.puzzle.text2sql.web.error.ClientError.InvalidModel
import com.chess.puzzle.text2sql.web.error.ClientError.MissingQuery
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class QueryPuzzleTest {

    @Test
    fun `toInput should return Success when query and deepseek model are valid`() {
        val query = "My Query"
        val model = "Deepseek"
        val request = QueryPuzzleRequest(query, model)
        val result = request.toInput()

        val expected = QueryPuzzleInput(query, ModelName.Deepseek)
        expectThat(result).isEqualTo(ResultWrapper.Success(expected))
    }

    @Test
    fun `toInput should return Success when query and mistral model are valid`() {
        val query = "My Query"
        val model = "Mistral"
        val request = QueryPuzzleRequest(query, model)
        val result = request.toInput()

        val expected = QueryPuzzleInput(query, ModelName.Mistral)
        expectThat(result).isEqualTo(ResultWrapper.Success(expected))
    }

    @Test
    fun `toInput should return Success when query and uppercased model are valid`() {
        val query = "My Query"
        val model = "DEEPSEEK"
        val request = QueryPuzzleRequest(query, model)
        val result = request.toInput()

        val expected = QueryPuzzleInput(query, ModelName.Deepseek)
        expectThat(result).isEqualTo(ResultWrapper.Success(expected))
    }

    @Test
    fun `toInput should return Success when query and undercased model are valid`() {
        val query = "My Query"
        val model = "deepseek"
        val request = QueryPuzzleRequest(query, model)
        val result = request.toInput()

        val expected = QueryPuzzleInput(query, ModelName.Deepseek)
        expectThat(result).isEqualTo(ResultWrapper.Success(expected))
    }

    @Test
    fun `toInput should return Success when query and random-cased model are valid`() {
        val query = "My Query"
        val model = "dEePSeEk"
        val request = QueryPuzzleRequest(query, model)
        val result = request.toInput()

        val expected = QueryPuzzleInput(query, ModelName.Deepseek)
        expectThat(result).isEqualTo(ResultWrapper.Success(expected))
    }

    @Test
    fun `toInput should return Failure with MissingQuery when query is null`() {
        val query = null
        val model = "Deepseek"
        val request = QueryPuzzleRequest(query, model)
        val result = request.toInput()

        val expected = listOf(MissingQuery)
        expectThat(result).isEqualTo(ResultWrapper.Failure(expected))
    }

    @Test
    fun `toInput should return Failure with InvalidModelName when model is invalid`() {
        val request = QueryPuzzleRequest(query = "SELECT * FROM users", model = "InvalidModel")
        val result = request.toInput()

        val expected = listOf(InvalidModel)
        expectThat(result).isEqualTo(ResultWrapper.Failure(expected))
    }

    @Test
    fun `toInput should return Success with default model when model is null`() {
        val query = "My Query"
        val request = QueryPuzzleRequest(query)
        val result = request.toInput()

        val expected = QueryPuzzleInput(query, ModelName.Deepseek)
        expectThat(result).isEqualTo(ResultWrapper.Success(expected))
    }

    @Test
    fun `toInput should return Failure with both MissingQuery and InvalidModelName when query is null and model is invalid`() {
        val request = QueryPuzzleRequest(query = null, model = "InvalidModel")
        val result = request.toInput()

        val expected = listOf(InvalidModel, MissingQuery)
        expectThat(result).isA<ResultWrapper.Failure<List<ClientError>>>().and {
            get { error }.contains(expected)
        }
    }
}
