import { writable } from 'svelte/store';
import type { GameProgress } from '$lib/types/gameProgress';
import type { PuzzleInstance } from '$lib/types/puzzleInstance';
import { Searches } from '$lib/stores/searchesStore';

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

export function updateCurrentGame(index: number, game: GameProgress) {
	currentGame.update((state) => {
		const updatedList = [
			...state.list.slice(0, index),
			{ ...state.list[index], progress: game },
			...state.list.slice(index + 1)
		];
		return {
			...state,
			list: updatedList
		};
	});
}
