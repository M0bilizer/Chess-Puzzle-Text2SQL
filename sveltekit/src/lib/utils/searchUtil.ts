import { get } from 'svelte/store';
import type { Puzzle } from '$lib/types/puzzle';
import type { PuzzleInstance } from '$lib/types/puzzleInstance';
import { convertUciToSan, getFirstMoveColor } from '$lib/utils/chessUtils';
import { haveGame } from '$lib/stores/currentGameStore';
import { loadFirstGame, loadFromSearchRecord, saveGame } from '$lib/utils/storeUtils';
import { isLoading } from '$lib/stores/isLoading';
import { addSearchResult, searches } from '$lib/stores/searchesStore';
import { toastFailure, toastInfo } from '$lib/utils/toastUtils';
import { getDataStub } from '$lib/utils/dataStub';

export enum Result {
	Success,
	BackendError,
	ClientError
}

export async function searchPuzzles(query: string): Promise<Result> {
	isLoading.set(true);
	if (haveGame()) saveGame();

	let result: Result;
	if (get(searches).get(query) == undefined) {
		try {
			const response = await fetch('/api/search', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({ query })
			});
			if (response.ok) {
				const json = await response.json();
				const list = _mapToInstance(json.data);
				loadFirstGame(query, list);
				addSearchResult(query, list);
				result = Result.Success;
			} else {
				console.error('Search failed:', response.statusText);
				toastFailure('Backend have internal error!', 'modal');
				result = Result.BackendError;
			}
		} catch (error) {
			console.error('Search error:', error);
			toastFailure('Cannot communicate with backend!', 'modal');
			result = Result.ClientError;
		}
	} else {
		loadFromSearchRecord(query);
		toastInfo('Reloaded game progress', 'root');
		result = Result.Success;
	}

	isLoading.set(false);
	return result;
}

export async function loadRandomPuzzle(query: string) {
	isLoading.set(true);
	const list = _mapToInstance(getDataStub());
	loadFirstGame(query, list);
	addSearchResult(query, list);
	toastInfo('Loaded random puzzles', 'root');
	isLoading.set(false);
}

function _mapToInstance(list: Puzzle[]): PuzzleInstance[] {
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
