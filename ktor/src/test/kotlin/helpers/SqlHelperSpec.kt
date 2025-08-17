package helpers

import com.chesspuzzletext2sql.helpers.isAllowed
import com.chesspuzzletext2sql.helpers.isValidSql
import com.chesspuzzletext2sql.helpers.preprocess
import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class IsValidSpec :
  DescribeSpec({
    describe("isValidSql()") {
      context("Valid SQL Statements") {
        listOf(
            "SELECT * FROM users",
            "WITH cte AS (SELECT 1) SELECT * FROM cte",
            "INSERT INTO users VALUES (1, 'John')",
            "UPDATE users SET name = 'John' WHERE id = 1",
            "DELETE FROM users WHERE id = 1",
            "CREATE TABLE users (id INT, name VARCHAR(100))",
            "ALTER TABLE users ADD COLUMN email VARCHAR(255)",
            "DROP TABLE users",
            "EXECUTE sp_refreshview 'v_users'",
          )
          .forEach { validSql ->
            it("accepts valid SQL: '${validSql.take(30)}...'") {
              expectThat(isValidSql(validSql)).isTrue()
            }
          }
      }

      context("Invalid SQL Statements") {
        listOf(
            "" to "empty string",
            "   " to "whitespace only",
            "SELECT" to "partial statement",
            "SELECT * FROM" to "incomplete statement",
            "SELECT * FROM; DELETE FROM users" to "query separation",
            "12345" to "random numbers",
            "This is not SQL" to "plain text",
            "SELECT * FROM `invalid`table`" to "invalid identifiers",
            String(CharArray(10001)) to "extremely large query",
          )
          .forEach { (invalidSql, reason) ->
            it("rejects invalid SQL ($reason)") { expectThat(isValidSql(invalidSql)).isFalse() }
          }
      }
    }
  })

class IsAllowedSpec :
  DescribeSpec({
    describe("isAllowed()") {
      context("Allowed SELECT Queries") {
        listOf(
            "SELECT * FROM table",
            "select id from users",
            "  SELECT name FROM customers  ",
            "SELECT * FROM table WHERE id = 1",
            "SELECT a.* FROM table_a a JOIN table_b b ON a.id = b.id",
            "WITH cte AS (SELECT 1) SELECT * FROM cte",
            "SELECT DISTINCT category FROM products",
            "SELECT COUNT(*) FROM users",
            "SELECT * FROM table ORDER BY name",
            "SELECT * FROM table LIMIT 10",
            "SELECT * FROM table OFFSET 5",
            "SELECT * FROM table GROUP BY category",
            "SELECT * FROM table1 UNION SELECT * FROM table2",
            "SELECT * FROM table1 INTERSECT SELECT * FROM table2",
            "SELECT * FROM table1 EXCEPT SELECT * FROM table2",
            "SELECT * FROM (SELECT * FROM nested) AS subquery",
          )
          .forEach { allowedSql ->
            it("should permit SELECT query: '${allowedSql.trim().take(30)}...'") {
              expectThat(isAllowed(allowedSql)).isTrue().and {
                get { isValidSql(allowedSql) }.isTrue()
              }
            }
          }
      }

      context("Blocked Non-SELECT Statements") {
        mapOf(
            "INSERT INTO table VALUES (1)" to "INSERT",
            "UPDATE users SET name = 'test'" to "UPDATE",
            "DELETE FROM logs" to "DELETE",
            "TRUNCATE TABLE temp_data" to "TRUNCATE",
            "CREATE TABLE new_table (id INT)" to "CREATE TABLE",
            "ALTER TABLE users ADD COLUMN age INT" to "ALTER TABLE",
            "DROP TABLE logs" to "DROP TABLE",
            "GRANT SELECT ON table TO user" to "GRANT",
            "REVOKE SELECT ON table FROM user" to "REVOKE",
            "BEGIN TRANSACTION" to "TRANSACTION",
            "COMMIT" to "COMMIT",
            "ROLLBACK" to "ROLLBACK",
            "EXPLAIN SELECT * FROM table" to "EXPLAIN",
            "WITH cte AS (SELECT 1) DELETE FROM table" to "CTE with DELETE",
            "MERGE INTO target USING source ON condition" to "MERGE",
            "CALL procedure_name()" to "CALL",
            "EXECUTE procedure_name" to "EXECUTE",
            "DECLARE @var INT" to "DECLARE",
            "SET @var = 1" to "SET variable",
          )
          .forEach { (blockedSql, operation) ->
            it("should block $operation statements") { expectThat(isAllowed(blockedSql)).isFalse() }
          }
      }

      context("Injection Attempts") {
        mapOf(
            "SELECT * FROM users; DROP TABLE users" to "DROP after SELECT",
            "SELECT * FROM users; -- comment\nDELETE FROM logs" to "DELETE in comment",
            "SELECT * FROM users WHERE id = 1; UPDATE users SET admin = true" to
              "UPDATE after WHERE",
            "SELECT * FROM users /* comment */ ; INSERT INTO logs VALUES ('hack')" to
              "INSERT in block comment",
            "SELECT * FROM users WHERE id = (SELECT id FROM config WHERE key = 'admin')" to
              "nested SELECT with suspicious content",
            "SELECT * FROM `; DROP TABLE users; --`" to "malicious identifiers",
            "SELECT * FROM \"users\"; DELETE FROM logs WHERE 1=1 --" to "quoted table with attack",
            "SELECT * FROM [users]; SHUTDOWN WITH NOWAIT" to "SQL Server shutdown attempt",
            "SELECT * FROM users WHERE id = 1 OR 1=1" to "always true condition",
            "SELECT * FROM users WHERE id = 1; SELECT 1 AS x INTO #temp" to "temp table creation",
          )
          .forEach { (maliciousSql, description) ->
            it("should block injection attempt: $description") {
              expectThat(isAllowed(maliciousSql)).isFalse()
            }
          }
      }

      context("Edge Cases") {
        mapOf(
            "" to "empty string",
            "   " to "whitespace only",
            "SELECT" to "incomplete SELECT",
            "FROM table" to "just FROM clause",
            "SELECT * FROM" to "incomplete query",
            "12345" to "random numbers",
            "This is not SQL" to "plain text",
          )
          .forEach { (input, description) ->
            it("should handle $description") { expectThat(isAllowed(input)).isFalse() }
          }
      }
    }
  })

class PreprocessSpec :
  DescribeSpec({
    describe("preprocess()") {
      context("SQL Extraction") {
        listOf(
            "sql: SELECT * FROM products" to "SELECT * FROM products LIMIT 100",
            "SQL SELECT * FROM orders" to "SELECT * FROM orders LIMIT 100",
            "\nSELECT * FROM items" to "SELECT * FROM items LIMIT 100",
            "  SELECT * FROM table  " to "SELECT * FROM table LIMIT 100",
            "SELECT * FROM data LIMIT 10" to "SELECT * FROM data LIMIT 10",
            "SELECT * FROM logs limit 5" to "SELECT * FROM logs limit 5",
          )
          .forEach { (input, expected) ->
            it("should handle '$input'") { expectThat(preprocess(input)).isEqualTo(expected) }
          }
      }

      context("Code Block Handling") {
        listOf(
            "```sql SELECT * FROM users```" to "SELECT * FROM users LIMIT 100",
            "```\nSELECT * FROM products\n```" to "SELECT * FROM products LIMIT 100",
            "Here's your query: ```sql\nSELECT id FROM customers```" to
              "SELECT id FROM customers LIMIT 100",
            "```\n\nSELECT * FROM table\n```" to "SELECT * FROM table LIMIT 100",
          )
          .forEach { (input, expected) ->
            it("should extract from code block: '$input'") {
              expectThat(preprocess(input)).isEqualTo(expected)
            }
          }
      }

      context("Whitespace Normalization") {
        listOf(
            "SELECT\n*\nFROM\ntable" to "SELECT * FROM table LIMIT 100",
            "SELECT\t*\tFROM\ttable" to "SELECT * FROM table LIMIT 100",
            "SELECT * FROM table\nWHERE id = 1" to "SELECT * FROM table WHERE id = 1 LIMIT 100",
          )
          .forEach { (input, expected) ->
            it("should normalize whitespace for '$input'") {
              expectThat(preprocess(input)).isEqualTo(expected)
            }
          }
      }
    }
  })
