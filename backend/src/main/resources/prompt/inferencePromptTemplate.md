# You are a text-to-SQL model. Based on the following MySQL database schema,
# convert the given natural language question into a SQL query. 
# Ensure the SQL query is returned in a single line without any unnecessary characters like "\n".
#
# t_puzzle (puzzle_id VARCHAR(255), rating INT, popularity INT, themes VARCHAR(255), opening_tags VARCHAR(255))
# Note that the `rating` column represents puzzle difficulty, where hard rating is > 1900 and easy rating is < 800.
# The `themes` and `opening_tags` columns contain space-separated values.
# Here is the list of valid themes: advancedPawn, advantage, anastasiaMate, arabianMate, attackingF2F7, attraction, backRankMate, bishopEndgame, bodenMate, capturingDefender, castling, clearance, crushing, defensiveMove, deflection, discoveredAttack, doubleBishopMate, doubleCheck, dovetailMate, enPassant, endgame, equality, exposedKing, fork, hangingPiece, hookMate, interference, intermezzo, kingsideAttack, knightEndgame, long, master, masterVsMaster, mate, mateIn1, mateIn2, mateIn3, mateIn4, mateIn5, middlegame, oneMove, opening, pawnEndgame, pin, promotion, queenEndgame, queenRookEndgame, queensideAttack, quietMove, rookEndgame, sacrifice, short, skewer, smotheredMate, superGM, trappedPiece, veryLong, xRayAttack, zugzwang
# When generating SQL queries, ONLY use themes from this list. Do not use any other themes.
#
# Here are five example records:
# 007XE, 645, 79, backRankMate fork mate mateIn2 middlegame short, Kings_Pawn_Game Kings_Pawn_Game_Leonardis_Variation
# 07Q5s, 2126, 48, advancedPawn advantage intermezzo middlegame short, Kings_Indian_Defense Kings_Indian_Defense_Normal_Variation
# 0kNf5, 2062, 70, advancedPawn discoveredAttack kingsideAttack mate mateIn4 middlegame pin promotion sacrifice veryLong, ""
#
# Below are some demonstrations to guide you
# 
# natural question: {{text0}}
# sql: {{sql0}}
#
# natural question: {{text1}}
# sql: {{sql1}}
#
# natural question: {{text2}}
# sql: {{sql2}}
natural question: {{prompt}}
sql: 
    