package com.chesspuzzletext2sql.helpers

import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.select.Select

fun isValidSql(sql: String): Boolean {
  if (sql.isBlank()) return false
  return try {
    CCJSqlParserUtil.parse(sql)
    true
  } catch (e: JSQLParserException) {
    false
  }
}

fun isAllowed(sql: String): Boolean {
  if (sql.isBlank()) return false
  if (
    !sql.trim().startsWith("SELECT", ignoreCase = true) &&
      !sql.trim().startsWith("WITH", ignoreCase = true)
  ) {
    return false
  }
  return try {
    val statement = CCJSqlParserUtil.parse(sql.trim())
    if (statement !is Select) return false
    if (sql.split(';').size > 1) return false

    val dangerousPatterns =
      listOf(
        Regex(
          "\\b(DROP|ALTER|TRUNCATE|CREATE|INSERT|UPDATE|DELETE|EXEC|CALL)\\b",
          RegexOption.IGNORE_CASE,
        ),
        Regex("\\b(OR\\s+1\\s*=\\s*1|UNION\\s+ALL\\s+SELECT)\\b", RegexOption.IGNORE_CASE),
        Regex("`|'|\"|/\\*|\\*/|--", RegexOption.IGNORE_CASE),
      )
    if (dangerousPatterns.any { it.containsMatchIn(sql) }) return false
    true
  } catch (e: JSQLParserException) {
    false
  }
}

fun preprocess(string: String, defaultLimit: Int = 100): String {
  var sql =
    string
      .substringAfter("```")
      .substringBefore("```")
      .replace("\r", "")
      .replace("\n", " ")
      .replace(Regex("\\s+"), " ")
      .substringAfter("sql")
      .substringAfter("SQL")
      .replace(":", "")
      .substringBefore(";")
      .replace("\"", "")
      .replace("'", "")
      .trim()

  val limitRegex = Regex("LIMIT\\s+\\d+\\s*$", RegexOption.IGNORE_CASE)

  if (!limitRegex.containsMatchIn(sql) && sql.isNotBlank()) {
    sql += " LIMIT $defaultLimit"
  }
  return sql
}
