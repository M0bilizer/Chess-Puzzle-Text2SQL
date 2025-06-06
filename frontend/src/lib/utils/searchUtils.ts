import type { Puzzle } from '$lib/types/puzzle';
import type { PuzzleInstance } from '$lib/types/puzzleInstance';
import { convertUciToSan, getFirstMoveColor } from '$lib/utils/chessUtils';
import { haveGame } from '$lib/stores/currentGameStore';
import { loadFirstGame, loadFromSearchRecord, saveGame } from '$lib/utils/storeUtils';
import { isLoading } from '$lib/stores/isLoading';
import { addSearchResult, hasSearched } from '$lib/stores/searchesStore';
import { toastFailure, toastInfo, toastModelFailure } from '$lib/utils/toastUtils';
import { getDataStub } from '$lib/utils/dataStub';
import { isStandardError, SearchResultEnum } from '$lib/enums/searchResultEnum';
import { ModelEnum } from '$lib/enums/modelEnum';
import type { SearchMetadata } from '$lib/types/SearchMetadata';

export async function searchPuzzles(query: string, model: ModelEnum): Promise<SearchResultEnum> {
	isLoading.set(true);
	if (haveGame()) saveGame();

	let result: SearchResultEnum;
	if (hasSearched(query)) {
		loadFromSearchRecord(query);
		toastInfo('Reloaded game progress', 'root');
		result = SearchResultEnum.Success;
	} else {
		try {
			const response = await fetch('/search', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({ query, model })
			});
			if (response.ok) {
				result = await _handleResponse(query, model, response);
			} else {
				toastFailure('Error when sending request', 'modal', query);
				result = SearchResultEnum.PostError;
			}
		} catch (error: unknown) {
			if (error instanceof Error) {
				toastFailure('Error when sending request:' + error.message, 'modal', query);
				result = SearchResultEnum.PostError;
			} else {
				toastFailure('Unknown Error when sending request:', 'modal', query);
				result = SearchResultEnum.UnknownClientError;
			}
		}
	}

	isLoading.set(false);
	return result;
}

export async function loadRandomPuzzle(query: string) {
	isLoading.set(true);
	const metadata: SearchMetadata = {
		query: query,
		model: null,
		maskedQuery: null,
		sql: null
	};
	const list = _mapToInstance(getDataStub());
	loadFirstGame(query, list);
	addSearchResult(query, metadata, list);
	toastInfo('Loaded random puzzles', 'root');
	isLoading.set(false);
}

async function _handleResponse(
	query: string,
	model: ModelEnum,
	response: Response
): Promise<SearchResultEnum> {
	const json: {
		status: SearchResultEnum;
		metadata: SearchMetadata;
		data: Puzzle[];
		message: string;
	} = await response.json();
	if (json.status == SearchResultEnum.Success) {
		const metadata = json.metadata;
		const data = _mapToInstance(json.data);
		loadFirstGame(query, data);
		addSearchResult(query, metadata, data);
		if (model !== ModelEnum.DeepSeek) toastInfo('Searched using alternative engine', 'root');
		toastInfo(`Found ${data.length} ${data.length === 1 ? 'puzzle' : 'puzzles'}`, 'root');
		return json.status;
	} else if (isStandardError(json.status)) {
		toastFailure(json.message, 'modal', query);
		return json.status;
	} else if (json.status == SearchResultEnum.BackendError) {
		if (model === ModelEnum.DeepSeek) toastModelFailure(json.message, 'modal', query);
		else toastFailure(json.message, 'modal', query);
		return json.status;
	} else {
		toastFailure(json.message, 'modal', query);
		return SearchResultEnum.UnknownServerStatusError;
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
