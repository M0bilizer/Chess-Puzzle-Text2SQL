import move_mp3 from '@/features/puzzle/assets/Move.mp3';
import capture_mp3 from '@/features/puzzle/assets/Capture.mp3';
import { Sound } from 'svelte-sound';
import { Chess } from 'chess.js';
import type { Puzzle } from './type.svelte';

const captured = new Sound(capture_mp3);
const move = new Sound(move_mp3);
export function playSound(isCapture: boolean) {
	// todo: should take in as a function prop instead
	if (isCapture) {
		captured.play();
	} else {
		move.play();
	}
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

export function getStartingFen(puzzle: Puzzle): string {
	const fen = puzzle.fen;
	const chess = new Chess();
	chess.load(fen);
	chess.move(puzzle.moves.trim().split(/\s+/).at(0) as string);
	return chess.fen();
}
