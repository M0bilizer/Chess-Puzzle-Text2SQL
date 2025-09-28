package com.chesspuzzletext2sql.helpers

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class SqlHelpersSpec :
    FunSpec({
        context("isValidSql") {
            withData(
                TestCase("blank string", ""),
                TestCase("whitespace only", " "),
                TestCase("invalid SQL", "SELECT * FROM"),
                TestCase("invalid statement", "INVALID SQL STATEMENT"),
            ) { (_, sql) ->
                expectThat(isValidSql(sql)).isFalse()
            }
            withData(
                TestCase("valid SELECT", "SELECT * FROM table"),
                TestCase(
                    "valid SELECT with conditions",
                    "SELECT id, name FROM users WHERE active = true",
                ),
            ) { (_, sql) ->
                expectThat(isValidSql(sql)).isTrue()
            }
        }

        context("isAllowed") {
            withData(
                TestCase("blank string", ""),
                TestCase("whitespace only", "   "),
                TestCase("INSERT statement", "INSERT INTO table VALUES (1)"),
                TestCase("DROP statement", "DROP TABLE users"),
                TestCase("UPDATE statement", "UPDATE users SET name = 'test'"),
                TestCase("multiple semicolons", "SELECT * FROM table; SELECT * FROM other"),
                TestCase("SQL injection OR 1=1", "SELECT * FROM users WHERE 1=1 OR 1=1"),
                TestCase(
                    "UNION ALL injection",
                    "SELECT * FROM users UNION ALL SELECT * FROM passwords",
                ),
                TestCase("DROP injection", "SELECT * FROM users; DROP TABLE users"),
                TestCase("backticks", "SELECT * FROM `users`"),
                TestCase("single quotes", "SELECT * FROM 'users'"),
                TestCase("double quotes", "SELECT * FROM \"users\""),
                TestCase("block comment", "SELECT * FROM users/*comment*/"),
                TestCase("line comment", "SELECT * FROM users -- comment"),
                TestCase("non-Select statement", "(SELECT * FROM table)"),
            ) { (_, sql) ->
                expectThat(isAllowed(sql)).isFalse()
            }

            withData(
                TestCase("simple SELECT", "SELECT * FROM table"),
                TestCase("SELECT with columns", "SELECT id, name FROM users"),
                TestCase("WITH statement", "WITH cte AS (SELECT 1) SELECT * FROM cte"),
            ) { (_, sql) ->
                expectThat(isAllowed(sql)).isTrue()
            }
        }

        context("preprocess") {
            withData(
                PreprocessTestCase(
                    "code block with sql",
                    "```sql SELECT * FROM table ```",
                    100,
                    "SELECT * FROM table LIMIT 100",
                ),
                PreprocessTestCase(
                    "code block without sql",
                    "```SELECT * FROM table```",
                    100,
                    "SELECT * FROM table LIMIT 100",
                ),
                PreprocessTestCase(
                    "sql prefix",
                    "sql SELECT * FROM table",
                    100,
                    "SELECT * FROM table LIMIT 100",
                ),
                PreprocessTestCase(
                    "SQL prefix",
                    "SQL SELECT * FROM table",
                    100,
                    "SELECT * FROM table LIMIT 100",
                ),
                PreprocessTestCase(
                    "semicolon removal",
                    "SELECT * FROM table;",
                    100,
                    "SELECT * FROM table LIMIT 100",
                ),
                PreprocessTestCase(
                    "double quotes removal",
                    "SELECT \"id\" FROM table",
                    100,
                    "SELECT id FROM table LIMIT 100",
                ),
                PreprocessTestCase(
                    "single quotes removal",
                    "SELECT 'id' FROM table",
                    100,
                    "SELECT id FROM table LIMIT 100",
                ),
                PreprocessTestCase(
                    "whitespace collapse",
                    "SELECT   *   FROM\n\t table",
                    100,
                    "SELECT * FROM table LIMIT 100",
                ),
                PreprocessTestCase(
                    "preserve existing LIMIT",
                    "SELECT * FROM table LIMIT 50",
                    100,
                    "SELECT * FROM table LIMIT 50",
                ),
                PreprocessTestCase(
                    "preserve existing lowercase limit",
                    "SELECT * FROM table limit 50",
                    100,
                    "SELECT * FROM table limit 50",
                ),
                PreprocessTestCase(
                    "add default LIMIT",
                    "SELECT * FROM table",
                    100,
                    "SELECT * FROM table LIMIT 100",
                ),
                PreprocessTestCase(
                    "custom default limit",
                    "SELECT * FROM table",
                    200,
                    "SELECT * FROM table LIMIT 200",
                ),
            ) { (_, input, limit, expected) ->
                expectThat(preprocess(input, limit)).isEqualTo(expected)
            }
        }
    })

data class TestCase(val description: String, val sql: String)

data class PreprocessTestCase(
    val description: String,
    val input: String,
    val limit: Int,
    val expected: String,
)
