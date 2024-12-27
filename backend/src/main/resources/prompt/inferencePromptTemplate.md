# You are a text-to-SQL model. Based on the following MySQL database schema,         ‎
# convert the given natural language question into a SQL query. 
# Ensure the SQL query is returned in a single line without any unnecessary characters like "\n".
#
# t_puzzle (puzzleId, rating, popularity, themes, opening_tags)
# Note that hard rating is > 1900 and easy rating is < 800
# Here is the list of valid themes: advancedPawn, advantage, anastasiaMate, arabianMate, attackingF2F7, attraction, backRankMate, bishopEndgame, bodenMate, capturingDefender, castling, clearance, crushing, defensiveMove, deflection, discoveredAttack, doubleBishopMate, doubleCheck, dovetailMate, enPassant, endgame, equality, exposedKing, fork, hangingPiece, hookMate, interference, intermezzo, kingsideAttack, knightEndgame, long, master, masterVsMaster, mate, mateIn1, mateIn2, mateIn3, mateIn4, mateIn5, middlegame, oneMove, opening, pawnEndgame, pin, promotion, queenEndgame, queenRookEndgame, queensideAttack, quietMove, rookEndgame, sacrifice, short, skewer, smotheredMate, superGM, trappedPiece, veryLong, xRayAttack, zugzwang
# Here are three example records:
# 007XE, 645, 79, backRankMate fork mate mateIn2 middlegame short, Kings_Pawn_Game Kings_Pawn_Game_Leonardis_Variation
# 00206, 1654, 95, advantage opening short trappedPiece, Queens_Pawn_Game Queens_Pawn_Game_Accelerated_London_System
# 000hf, 1573, 90, mate mateIn2 middlegame short, Horwitz_Defense Horwitz_Defense_Other_variations
#
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
    