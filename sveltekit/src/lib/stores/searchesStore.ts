import { writable } from 'svelte/store';
import type { PuzzleInstance } from '$lib/types/puzzleInstance';
import type { GameProgress } from '$lib/types/gameProgress';

export const Searches = writable<Map<string, PuzzleInstance[]>>(new Map());

export function addSearchResult(query: string, results: PuzzleInstance[]) {
	Searches.update((state) => {
		state.set(query, results);
		return state;
	});
}

export function updateSearchResult(query: string, index: number, game: GameProgress) {
	Searches.update((state) => {
		const result = state.get(query);
		if (!result) {
			return state;
		}
		const updated = result.with(index, {
			...result[index],
			progress: game.hasWon ? game : result[index].progress
		});
		state.set(query, updated);
		return state;
	});
}

export function deleteSearchResult(query: string) {
	Searches.update((state) => {
		state.delete(query);
		return state;
	});
}

export function deleteAllSearchResult() {
	Searches.set(new Map());
}
