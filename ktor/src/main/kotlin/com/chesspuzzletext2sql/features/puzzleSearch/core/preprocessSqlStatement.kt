package com.chesspuzzletext2sql.features.puzzleSearch.core

fun preprocessSqlStatement(string: String, defaultLimit: Int = 100): String {
    var sql =
        string
            .substringAfter("```")
            .substringBefore("```")
            .replace("\r", "") // Remove carriage returns
            .replace("\n", " ") // Replace newlines with single spaces
            .replace(Regex("\\s+"), " ") // Collapse multiple spaces
            .substringAfter("sql")
            .substringAfter("SQL")
            .replace(":", "")
            .substringBefore(";")
            .replace("\"", "")
            .trim()

    val limitRegex = Regex("LIMIT\\s+\\d+\\s*$", RegexOption.IGNORE_CASE)

    if (!limitRegex.containsMatchIn(sql) && sql.isNotBlank()) {
        sql += " LIMIT $defaultLimit"
    }
    return sql
}
