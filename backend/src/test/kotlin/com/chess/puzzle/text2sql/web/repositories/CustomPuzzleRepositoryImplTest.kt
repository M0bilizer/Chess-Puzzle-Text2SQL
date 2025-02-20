package com.chess.puzzle.text2sql.web.repositories

import com.chess.puzzle.text2sql.web.entities.Puzzle
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.EntityManager
import jakarta.persistence.Query
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo

class CustomPuzzleRepositoryImplTest {

    private val mockEntityManager = mockk<EntityManager>()
    private val customPuzzleRepository = CustomPuzzleRepositoryImpl(mockEntityManager)

    @Test
    fun `executeSqlQuery should return a list of Puzzle entities`() {
        // Arrange
        val sqlCommand = "SELECT * FROM Puzzle"
        val protected = "SELECT * FROM Puzzle LIMIT 100"
        val mockQuery = mockk<Query>()
        val mockPuzzleList =
            listOf(
                Puzzle(
                    id = 0,
                    puzzleId = "00sHx",
                    fen = "r3k2r/1pp1nQpp/3p4/1P2p3/4P3/B1PP1b2/B5PP/5K2 b k - 0 17",
                    moves = "e8d7 a2e6 d7d8 f7f8",
                    rating = 1760,
                    ratingDeviation = 80,
                    popularity = 83,
                    nbPlays = 72,
                    themes = "mate mateIn2 middlegame short",
                    gameUrl = "https://lichess.org/yyznGmXs/black#34",
                    openingTags = "Italian_Game Italian_Game_Classical_Variation",
                ),
                Puzzle(
                    id = 1,
                    puzzleId = "00sJ9",
                    fen = "r3r1k1/p4ppp/2p2n2/1p6/3P1qb1/2NQR3/PPB2PP1/R1B3K1 w - - 5 18",
                    moves = "e3g3 e8e1 g1h2 e1c1 a1c1 f4h6 h2g1 h6c1",
                    rating = 2671,
                    ratingDeviation = 105,
                    popularity = 87,
                    nbPlays = 325,
                    themes = "advantage attraction fork middlegame sacrifice veryLong",
                    gameUrl = "https://lichess.org/gyFeQsOE#35",
                    openingTags = "French_Defense French_Defense_Exchange_Variation",
                ),
            )

        // Mock the EntityManager.createNativeQuery method
        every { mockEntityManager.createNativeQuery(protected, Puzzle::class.java) } returns
            mockQuery

        // Mock the Query.resultList method
        every { mockQuery.resultList } returns mockPuzzleList

        // Act
        val result = customPuzzleRepository.executeSqlQuery(sqlCommand)

        // Assert
        expectThat(result).isEqualTo(mockPuzzleList)

        // Verify that the EntityManager.createNativeQuery method was called
        verify(exactly = 1) { mockEntityManager.createNativeQuery(protected, Puzzle::class.java) }
    }

    @Test
    fun `executeSqlQuery should throw RuntimeException for invalid SQL`() {
        // Arrange
        val sqlCommand = "INVALID SQL COMMAND"
        val protected = "INVALID SQL COMMAND LIMIT 100"

        // Mock the EntityManager.createNativeQuery method to throw an exception
        every { mockEntityManager.createNativeQuery(protected, Puzzle::class.java) } throws
            RuntimeException("Invalid SQL")

        // Act & Assert
        expectThrows<RuntimeException> { customPuzzleRepository.executeSqlQuery(sqlCommand) }

        // Verify that the EntityManager.createNativeQuery method was called
        verify(exactly = 1) { mockEntityManager.createNativeQuery(protected, Puzzle::class.java) }
    }
}
