package com.chess.puzzle.text2sql.web.repositories

import com.chess.puzzle.text2sql.web.entities.Puzzle
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import strikt.api.expectThat
import strikt.assertions.get
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo

@DataJpaTest
@ActiveProfiles("test")
class PuzzleRepositoryIntegrationTest {
    @Autowired private lateinit var puzzleRepository: PuzzleRepository

    private val puzzles =
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
            Puzzle(
                id = 2,
                puzzleId = "00sJb",
                fen = "Q1b2r1k/p2np2p/5bp1/q7/5P2/4B3/PPP3PP/2KR1B1R w - - 1 17",
                moves = "d1d7 a5e1 d7d1 e1e3 c1b1 e3b6",
                rating = 2235,
                ratingDeviation = 76,
                popularity = 97,
                nbPlays = 64,
                themes = "advantage fork long",
                gameUrl = "https://lichess.org/kiuvTFoE#33",
                openingTags = "Sicilian_Defense Sicilian_Defense_Dragon_Variation",
            ),
        )

    @BeforeEach
    fun setUp() {
        puzzleRepository.saveAll(puzzles)
    }

    @Test
    fun `executeSqlQuery should return a list of Puzzle entities`() {
        val sql = "SELECT * FROM t_puzzle"
        val result = puzzleRepository.executeSqlQuery(sql)
        expectThat(result).isEqualTo(puzzles)
    }

    @Test
    fun `executeSqlQuery should return the correct list of Puzzle entities`() {
        val sql = "SELECT * FROM t_puzzle WHERE id = 0"
        val result = puzzleRepository.executeSqlQuery(sql)
        expectThat(result) {
            hasSize(1)
            get(0).and { get { id }.isEqualTo(0) }
        }
    }

    @Test
    fun `executeSqlQuery should return empty list if no result matches`() {
        val sql = "SELECT * FROM t_puzzle WHERE id = 3"
        val result = puzzleRepository.executeSqlQuery(sql)
        expectThat(result) { hasSize(0) }
    }
}
