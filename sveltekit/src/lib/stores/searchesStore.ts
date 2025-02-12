import { get, writable } from 'svelte/store';
import type { PuzzleInstance } from '$lib/types/puzzleInstance';
import type { GameProgress } from '$lib/types/gameProgress';
import type { SearchMetadata } from '$lib/types/SearchMetadata';
import type { Search } from '$lib/types/SearchInformation';

export const searches = writable<Map<string, Search>>(new Map());

export function addSearchResult(
	query: string,
	metadata: SearchMetadata,
	results: PuzzleInstance[]
) {
	searches.update((state) => {
		state.set(query, { metadata: metadata, data: results });
		return state;
	});
}

export function updateSearchResult(query: string, index: number, game: GameProgress) {
	searches.update((state) => {
		const result = state.get(query);
		if (!result) {
			return state;
		}
		const updated = result.data.with(index, {
			...result.data[index],
			progress: game.hasWon ? game : result.data[index].progress
		});
		state.set(query, { metadata: result.metadata, data: updated });
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
