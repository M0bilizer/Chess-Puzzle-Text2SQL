import type { Puzzle } from '$lib/types/puzzle';
import type { PuzzleInstance } from '$lib/types/puzzleInstance';
import { convertUciToSan, getFirstMoveColor } from '$lib/utils/chessUtils';
import { haveGame } from '$lib/stores/currentGameStore';
import { loadFirstGame, loadFromSearchRecord, saveGame } from '$lib/utils/storeUtils';
import { isLoading } from '$lib/stores/isLoading';
import { addSearchResult, hasSearched } from '$lib/stores/searchesStore';
import { toastFailure, toastInfo } from '$lib/utils/toastUtils';
import { getDataStub } from '$lib/utils/dataStub';

export enum Result {
	Success,
	BackendError,
	ConnectionError,
	ConfigurationError,
	PostError,
	UnknownError
}

export async function searchPuzzles(query: string): Promise<Result> {
	isLoading.set(true);
	if (haveGame()) saveGame();

	let result: Result;
	if (hasSearched(query)) {
		loadFromSearchRecord(query);
		toastInfo('Reloaded game progress', 'root');
		result = Result.Success;
	} else {
		try {
			const response = await fetch('/api/search', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({ query })
			});
			if (response.ok) {
				result = await _handleResponse(query, response);
			} else {
				toastFailure('Error when sending request', 'modal', query);
				result = Result.PostError;
			}
		} catch (error: unknown) {
			if (error instanceof Error) {
				toastFailure('Error when sending request:' + error.message, 'modal', query);
				result = Result.PostError;
			} else {
				toastFailure('Unknown Error when sending request:', 'modal', query);
				result = Result.UnknownError;
			}
		}
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

async function _handleResponse(query: string, response: Response): Promise<Result> {
	const json = await response.json();
	if (json.status == 'success') {
		const list = _mapToInstance(json.data);
		loadFirstGame(query, list);
		addSearchResult(query, list);
		return Result.Success;
	} else if (json.status == 'connectionFailure') {
		toastFailure(json.message, 'modal', query);
		return Result.ConnectionError;
	} else if (json.status == 'backendFailure') {
		toastFailure(json.message, 'modal', query);
		return Result.BackendError;
	} else if (json.status == 'configurationError') {
		toastFailure(json.message, 'modal', query);
		return Result.ConfigurationError;
	} else {
		toastFailure(json.message, 'modal', query);
		return Result.UnknownError;
	}
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
