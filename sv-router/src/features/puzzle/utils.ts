import type { Game, Puzzle } from './type.svelte';

export function puzzleToGame(puzzle: Puzzle): Game {
	const moveList = puzzle.moves.split(' ');

	const moves: { computer: string; player: string }[] = [];

	for (let i = 0; i < moveList.length; i += 2) {
		moves.push({
			computer: moveList[i],
			player: moveList[i + 1] || ''
		});
	}

	return {
		fen: puzzle.fen,
		moves
	};
}
