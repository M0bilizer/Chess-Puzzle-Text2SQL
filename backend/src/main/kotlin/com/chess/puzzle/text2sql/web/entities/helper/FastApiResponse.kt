package com.chess.puzzle.text2sql.web.entities.helper

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the data format response by the Sentence Transformer Microservice.
 *
 * This class is used by the Sentence Transformer Helper to process responses from the microservice.
 *
 * @see [com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper]
 */
@Serializable
data class FastApiResponse(
    /**
     * The status of the response.
     *
     * Note that this is different from the HTTP response status. It is a string of either "success"
     * or "fail". This status is specific to the microservice's internal processing and should not
     * be confused with HTTP status codes.
     */
    @SerialName("status") val status: String,
    /**
     * The maskedQuery of the response.
     *
     * When submitting a query to the sentence transformer microservice, the microservice will
     * perform schema masking. Schema Masking will hide database keywords from the text. It is used
     * to support in finding similar demonstration.
     *
     * @sample "Give me a <opening_tag> puzzle which have a <theme>"
     */
    @SerialName("masked_query") val maskedQuery: String,
    /**
     * The list of the three most similar demonstration.
     *
     * When submitting a query to the sentence transformer microservice, the microservice will find
     * three demonstration most similar to it. The similarity is based on whether the associated SQL
     * would be similar. Each demonstration is represented by a [Demonstration] object, which
     * contains a text and sql.
     *
     * @sample [ Demonstration("Italian Defense","SELECT * FROM t_puzzle WHERE opening_tags LIKE '%Italian_Defense%'"), Demonstration("Recommend a strategy for playing against the King's Indian Defense.", "SELECT * FROM t_puzzle WHERE opening_tags LIKE '%Kings_Indian_Defense%'"), Demonstration("I prefer open positions", "SELECT * FROM t_puzzle WHERE opening_tags LIKE '%Italian%' OR opening_tags LIKE '%Ruy_Lopez%' OR opening_tags LIKE '%Scotch%'") ]
     */
    @SerialName("data") val data: List<Demonstration>,
)
