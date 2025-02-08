import { Searches, updateSearchResult } from '$lib/stores/searchesStore';
import { currentGame, getNextGameIndex, updateCurrentGame } from '$lib/stores/currentGameStore';
import type { PuzzleInstance } from '$lib/types/puzzleInstance';
import { get } from 'svelte/store';
import { isInJump, tearDown } from '$lib/stores/jumpStore';

export function loadFirstGame(query: string, list: PuzzleInstance[]) {
	const first = list[0];
	currentGame.set({
		query: query,
		list: list,
		index: 0,
		game: first.progress
	});
}

export function saveGame(): boolean {
	const { query, index, game } = get(currentGame);
	if (query == '' || game.fen == '') {
		return false;
	}
	if (isInJump()) tearDown();
	updateCurrentGame(index, game);
	updateSearchResult(query, index, game);
	return true;
}

export function loadNextGame(): boolean {
	let isNextGameLoaded = false;

	currentGame.update((state) => {
		const nextGameIndex = getNextGameIndex();

		if (nextGameIndex !== -1 && nextGameIndex < state.list.length) {
			isNextGameLoaded = true;
			return {
				...state,
				index: nextGameIndex,
				game: state.list[nextGameIndex].progress
			};
		}

		return state;
	});

	return isNextGameLoaded;
}

export function loadGame(index: number) {
	saveGame();
	currentGame.update((state) => ({
		...state,
		index: index,
		game: state.list[index].progress
	}));
}

export function loadFromSearchRecord(query: string) {
	if (get(currentGame).query === query) return;
	saveGame();
	const result = get(Searches).get(query) as PuzzleInstance[];
	let index = result.findIndex((value) => !value.progress.hasWon);
	if (index === -1) {
		index = result.length - 1;
	}
	currentGame.set({
		query: query,
		list: result,
		index: index,
		game: result[index].progress
	});
}
