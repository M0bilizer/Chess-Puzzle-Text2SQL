import { get, writable } from 'svelte/store';
import type { GameProgress } from '$lib/types/gameProgress';
import type { PuzzleInstance } from '$lib/types/puzzleInstance';
import { playMove } from '$lib/utils/chessUtils';

export interface currentGameState {
	query: string;
	list: PuzzleInstance[];
	index: number;
	game: GameProgress;
}

export const currentGame = writable<currentGameState>({
	query: '',
	list: [],
	index: 0,
	game: {
		fen: '',
		orientation: 'w',
		moves: [],
		moveIndex: 0,
		hasWon: false
	}
});

export function haveGame() {
	return get(currentGame).list.length > 0;
}

export function isAllFinished() {
	return get(currentGame).list.findIndex((value) => !value.progress.hasWon) === -1;
}

export function getNextGameIndex() {
	return get(currentGame).list.findIndex((value) => !value.progress.hasWon);
}

export function updateCurrentGame(index: number, game: GameProgress) {
	currentGame.update((state) => {
		const updatedList = [
			...state.list.slice(0, index),
			{ ...state.list[index], progress: game },
			...state.list.slice(index + 1)
		];
		return {
			...state,
			list: updatedList
		};
	});
}

export function clearCurrentGame() {
	currentGame.set({
		query: '',
		list: [],
		index: 0,
		game: {
			fen: '',
			orientation: 'w',
			moves: [],
			moveIndex: 0,
			hasWon: false
		}
	});
}

// export function skipMove() {
// 	currentGame.update((state) => {
// 		let fen = state.game.fen
// 		fen = playMove(fen, state.game.moves[state.game.moveIndex])
// 		return {
// 			...state,
// 			game: {
// 				...state.game,
// 				fen: fen,
// 				moveIndex: state.game.moveIndex + 1,
// 				hasWon: state.game.moveIndex + 1 === state.game.moves.length - 1,
// 			}
// 		}
// 	});
// }

export function skipCurrentGame() {
	currentGame.update((state) => {
		const puzzleInstance = state.list[state.index];
		let fen = puzzleInstance.puzzle.fen;
		puzzleInstance.progress.moves.forEach((value) => (fen = playMove(fen, value)));

		return {
			...state,
			game: {
				...state.game,
				fen: fen,
				moveIndex: state.game.moves.length - 1,
				hasWon: true
			}
		};
	});
}
