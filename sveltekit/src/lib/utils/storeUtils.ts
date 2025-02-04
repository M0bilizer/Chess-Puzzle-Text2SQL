import { isLoading } from '$lib/stores/isLoading';
import type { Puzzle } from '$lib/types/puzzle';
import { getDataStub } from '$lib/utils/dataStub';
import { KOTLIN_SPRING_URL } from '$lib/constants/endpoints';
import { toast } from 'svelte-sonner';
import { convertUciToSan, getFirstMoveColor } from '$lib/utils/chessUtils';
import { addSearchResult, Searches, updateSearchResult } from '$lib/stores/searchesStore';
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

export async function searchPuzzles(
	query: string,
	debug: 'stub' | 'ping' | 'live' = 'live'
): Promise<boolean> {
	try {
		isLoading.set(true);

		if (get(Searches).get(query) == null) {
			const result: Puzzle[] = await getResponse(query, debug);
			const list = mapToInstance(result);

			loadFirstGame(query, list);
			addSearchResult(query, list);
		} else {
			loadFromSearchRecord(query);
		}

		isLoading.set(false);
		return true;
	} catch (error) {
		console.error('Search failed:', error);
		toast.message(error.message);
		isLoading.set(false);
		return false;
	}
}

async function getResponse(query: string, debug: 'stub' | 'ping' | 'live'): Promise<Puzzle[]> {
	let res;
	let result: Puzzle[];
	switch (debug) {
		case 'stub':
			result = getDataStub();
			break;

		case 'ping':
			res = await fetch(`${KOTLIN_SPRING_URL}/debug/db`, {
				method: 'GET'
			});
			if (!res.ok) throw new Error(`HTTP error! Status: ${res.status}`);
			result = (await res.json()).data;
			break;

		case 'live': {
			res = await fetch(`${KOTLIN_SPRING_URL}/queryPuzzle`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ query })
			});
			if (!res.ok) throw new Error(`HTTP error! Status: ${res.status}`);
			const responseData = await res.json();
			if (responseData.status !== 'success') {
				throw new Error(`API error: ${responseData.status}`);
			}
			result = responseData.data;
			break;
		}

		default:
			throw new Error(`Invalid debug mode: ${debug}`);
	}
	return result;
}

function mapToInstance(list: Puzzle[]): PuzzleInstance[] {
	return list.map((puzzle) => {
		const fen = puzzle.fen;
		const moves = convertUciToSan(fen, puzzle.moves);
		const orientation = getFirstMoveColor(puzzle.fen) === 'w' ? 'b' : 'w';
		const puzzleInstance: PuzzleInstance = {
			puzzle: puzzle,
			progress: {
				fen: fen,
				orientation: orientation,
				moves: moves,
				moveIndex: 0,
				hasWon: false
			}
		};
		return puzzleInstance;
	});
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
