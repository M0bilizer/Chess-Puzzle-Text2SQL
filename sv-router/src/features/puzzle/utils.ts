import type { Game, Puzzle } from './type.svelte';
import move_mp3 from '@/features/puzzle/assets/Move.mp3';
import capture_mp3 from '@/features/puzzle/assets/Capture.mp3';

export function playSound(isCapture: boolean) {
	if (isCapture) {
		const audio = new Audio(capture_mp3);
		audio.play().catch();
	} else {
		const audio = new Audio(move_mp3);
		audio.play().catch();
	}
}

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

export function getPlayerColor(fen: string): 'w' | 'b' {
	const fields = fen.split(' ');

	const activeColor = fields[1];

	if (activeColor !== 'w' && activeColor !== 'b') {
		throw new Error('Invalid FEN: Active color must be "w" or "b".');
	}

	// The player is the 2nd move
	return activeColor === 'w' ? 'b' : 'w';
}
