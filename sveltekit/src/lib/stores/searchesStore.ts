import { get, writable } from 'svelte/store';
import type { PuzzleInstance } from '$lib/types/puzzleInstance';
import type { GameProgress } from '$lib/types/gameProgress';

export const searches = writable<Map<string, PuzzleInstance[]>>(new Map());

export function addSearchResult(query: string, results: PuzzleInstance[]) {
	searches.update((state) => {
		state.set(query, results);
		return state;
	});
}

export function updateSearchResult(query: string, index: number, game: GameProgress) {
	searches.update((state) => {
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

export function hasSearched(query: string): boolean {
	return get(searches).has(query);
}

export function deleteSearchResult(query: string) {
	searches.update((state) => {
		state.delete(query);
		return state;
	});
}

export function deleteAllSearchResult() {
	searches.set(new Map());
}
