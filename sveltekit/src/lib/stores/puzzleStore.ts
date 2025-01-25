// src/stores/currentPuzzles.js
import { writable } from 'svelte/store';
import { KOTLIN_SPRING_URL } from '$lib/constants/endpoints';
import type { Puzzle } from '$lib/types/puzzle';
import { toast } from 'svelte-sonner';
import { getDataStub } from '$lib/stores/dataStub';
import { convertUciToSan, getFirstMoveColor } from '$lib/utils/chessUtils';
import type { Search } from '$lib/types/search';

interface gameProgressState {
	currentFen: string;
	orientation: 'w' | 'b';
	moves: string[];
	moveIndex: number;
	hasWon: boolean;
}

interface puzzleListState {
	puzzles: Puzzle[];
	currentPuzzle: number;
}

export const currentGameProgress = writable<gameProgressState>({
	currentFen: '',
	orientation: 'w',
	moves: [],
	moveIndex: 0,
	hasWon: false
});

export const puzzleList = writable<puzzleListState>({ puzzles: [], currentPuzzle: 0 });
export const pastSearches = writable<Search[]>([]);
export const isLoading = writable<boolean>(false);

export function loadAsCurrentGame(puzzle: Puzzle) {
	const fen = puzzle.fen;
	const moves = convertUciToSan(fen, puzzle.moves);
	const orientation = getFirstMoveColor(puzzle.fen) === 'w' ? 'b' : 'w';
	currentGameProgress.set({ currentFen: fen, orientation, moves, moveIndex: 0, hasWon: false });
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

		loadAsCurrentGame(result[0]);
		puzzleList.set({ puzzles: result, currentPuzzle: 0 });
		pastSearches.update((pastResults) => [...pastResults, { query: query, result: result }]);

		isLoading.set(false);
		return true;
	} catch (error) {
		console.error('Search failed:', error);
		toast.message(error.message);
		isLoading.set(false);
		return false;
	}
}
