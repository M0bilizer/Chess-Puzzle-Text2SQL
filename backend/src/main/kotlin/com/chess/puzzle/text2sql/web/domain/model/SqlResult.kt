package com.chess.puzzle.text2sql.web.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents the SQL statement and status created by models in the ablation study.
 *
 * This class is used for writing the SQL statement and its status into a result file.
 *
 * @see [com.chess.puzzle.text2sql.web.entities.BenchmarkResult]
 * @see [com.chess.puzzle.text2sql.web.service.BenchmarkService]
 */
@Serializable
data class SqlResult(
    /**
     * The SQL statement created by the model.
     *
     * This SQL statement is generated by the model during the ablation study.
     */
    val sql: String,
    /**
     * The status of the SQL statement.
     *
     * This field is initially empty and will be manually modified after the SQL statement is
     * created. It is used to indicate whether the SQL statement is valid and whether it fulfill the
     * natural question.
     */
    val status: String,
)
