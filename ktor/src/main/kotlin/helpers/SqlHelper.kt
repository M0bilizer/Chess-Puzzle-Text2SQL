package com.chesspuzzletext2sql.helpers

import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.parser.CCJSqlParserUtil

fun isValidSql(sql: String): Boolean {
    return try {
        CCJSqlParserUtil.parse(sql)
        true
    } catch (e: JSQLParserException) {
        false
    }
}

fun isAllowed(sql: String): Boolean {
    return sql.trim().startsWith("SELECT", ignoreCase = true)
}

fun preprocess(string: String, defaultLimit: Int = 100): String {
    var sql =
        string
            .substringAfter("```")
            .substringBefore("```")
            .replace("\n", "")
            .substringAfter("sql")
            .replace(":", "")
            .substringBefore(";")
            .substringBefore("\r")
            .replace("\"", "")
            .trim()

    val limitRegex = Regex("LIMIT\\s+\\d+\\s*$", RegexOption.IGNORE_CASE)

    if (!limitRegex.containsMatchIn(sql)) {
        sql = sql.replace(";", "").trim()
        sql += " LIMIT $defaultLimit"
    }

    return sql
}
