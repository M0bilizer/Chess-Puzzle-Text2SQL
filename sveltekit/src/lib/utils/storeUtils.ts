import { isLoading } from '$lib/stores/isLoading';
import type { Puzzle } from '$lib/types/puzzle';
import { getDataStub } from '$lib/utils/dataStub';
import { KOTLIN_SPRING_URL } from '$lib/constants/endpoints';
import { toast } from 'svelte-sonner';
import { convertUciToSan, getFirstMoveColor } from '$lib/utils/chessUtils';
import { addSearchResult, updateSearchResult } from '$lib/stores/searchesStore';
import { currentGame, updateCurrentGame } from '$lib/stores/currentGameStore';
import type { PuzzleInstance } from '$lib/types/puzzleInstance';
import { get } from 'svelte/store';

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
	const state = get(currentGame);
	const { query, index, game } = state;
	if (query == null || index == null || game == null) {
		return false;
	}
	updateCurrentGame(index, game);
	updateSearchResult(query, index, game);
	return true;
}

export function isGameInProgress(): boolean {
	return get(currentGame).list.length > 0;
}

export function isLastGame(): boolean {
	const state = get(currentGame);
	return !(state.index + 1 < state.list.length);
}

export function loadNextGame(): boolean {
	let isNextGameLoaded = false;

	currentGame.update((state) => {
		if (state.index + 1 < state.list.length) {
			isNextGameLoaded = true;
			return {
				...state,
				index: state.index + 1,
				game: state.list[state.index + 1].progress
			};
		} else {
			return state;
		}
	});

	return isNextGameLoaded;
}

export async function searchPuzzles(
	query: string,
	debug: 'stub' | 'ping' | 'live' = 'live'
): Promise<boolean> {
	try {
		isLoading.set(true);

		let res: Response;
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

		const list = mapToInstance(result);
		loadFirstGame(query, list);
		addSearchResult(query, list);

		isLoading.set(false);
		return true;
	} catch (error) {
		console.error('Search failed:', error);
		toast.message(error.message);
		isLoading.set(false);
		return false;
	}
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
