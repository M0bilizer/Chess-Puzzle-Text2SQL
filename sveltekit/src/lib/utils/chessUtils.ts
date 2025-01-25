import { Chess } from 'chess.js';
import { loadAsCurrentGame, puzzleList } from '$lib/stores/puzzleStore';

export function getFirstMoveColor(fen: string): 'w' | 'b' {
	const fields = fen.split(' ');

	const activeColor = fields[1];

	if (activeColor !== 'w' && activeColor !== 'b') {
		throw new Error('Invalid FEN: Active color must be "w" or "b".');
	}

	return activeColor;
}

export function convertUciToSan(fen: string, uciMove: string): string[] {
	const chess = new Chess(fen);

	// If the input is a single move (e.g., "f2g3")
	if (!uciMove.includes(' ')) {
		const move = chess.move({
			from: uciMove.slice(0, 2), // Extract "f2"
			to: uciMove.slice(2, 4), // Extract "g3"
			promotion: uciMove.length > 4 ? uciMove[4] : undefined // Handle promotions (e.g., "e7e8q")
		});

		if (!move) {
			throw new Error(`Invalid UCI move: ${uciMove}`);
		}

		return [move.san]; // Return the SAN notation (e.g., "fxg3" or "Nf3")
	}

	// If the input is a list of moves (e.g., "f2g3 e6e7 b2b1")
	const uciMoves = uciMove.split(' ');
	const sanMoves: string[] = [];

	for (const move of uciMoves) {
		const result = chess.move({
			from: move.slice(0, 2),
			to: move.slice(2, 4),
			promotion: move.length > 4 ? move[4] : undefined
		});

		if (!result) {
			throw new Error(`Invalid UCI move: ${move}`);
		}

		sanMoves.push(result.san); // Add the SAN notation to the list
	}

	return sanMoves; // Return the list of SAN moves
}

export function playMove(fen: string, sanMove: string): string {
	const chess = new Chess(fen);

	const move = chess.move(sanMove);

	if (!move) {
		throw new Error(`Invalid move: ${sanMove}`);
	}

	return chess.fen();
}
