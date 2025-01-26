import { writable } from 'svelte/store';
import type { GameProgress } from '$lib/types/gameProgress';
import type { PuzzleInstance } from '$lib/types/puzzleInstance';

export interface currentGameState {
	query: string;
	list: PuzzleInstance[];
	index: number;
	game: GameProgress;
}

export const currentGame = writable<currentGameState>({
	query: '',
	list: [],
	index: 0,
	game: {
		fen: '',
		orientation: 'w',
		moves: [],
		moveIndex: 0,
		hasWon: false
	}
});
