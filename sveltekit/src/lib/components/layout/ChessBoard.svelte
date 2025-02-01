<script lang="ts">
	import { Chess } from 'svelte-chess';
	import { playMove } from '$lib/utils/chessUtils';
	import { currentGame, type currentGameState } from '$lib/stores/currentGameStore';
	import { get } from 'svelte/store';
	import { jump, isInJump } from '$lib/stores/jumpStore';

	let chess: Chess;
	let orientation: 'w' | 'b' = $state('w');
	let isJumping = $state(false);

	function loadGame(state: currentGameState) {
		chess.load(state.game.fen);
		if (orientation !== state.game.orientation) {
			chess.toggleOrientation();
		}
	}

	function isPlayerMove() {
		return get(currentGame).game.moveIndex % 2 !== 0;
	}

	function isCorrectMove(move: string) {
		return get(currentGame).game.moves[get(currentGame).game.moveIndex] === move;
	}

	function isLastMove() {
		return get(currentGame).game.moveIndex > get(currentGame).game.moves.length - 1;
	}

	function initJump() {
		const currentGameStore: currentGameState = get(currentGame);
		chess.load(currentGameStore.list[currentGameStore.index].puzzle.fen);
		currentGameStore.game.moves
			.slice(0, currentGameStore.game.moveIndex)
			.forEach((move) => chess.move(move));
	}

	function redoJump() {
		const currentGameStore: currentGameState = get(currentGame);
		chess.load(currentGameStore.list[currentGameStore.index].puzzle.fen);
		currentGameStore.game.moves.slice(0, get(jump).current).forEach((move) => chess.move(move));
	}

	function undoJump() {
		const currentGameStore: currentGameState = get(currentGame);
		chess.load(currentGameStore.list[currentGameStore.index].puzzle.fen);
		currentGameStore.game.moves.slice(0, get(jump).current + 1).forEach((move) => chess.move(move));
		chess.undo();
	}

	currentGame.subscribe((state: currentGameState) => {
		if (state.game.fen !== '') loadGame(state);
		if (state.game.hasWon == false) {
			if (!isPlayerMove()) {
				if (isLastMove()) {
					setTimeout(() => {
						currentGame.update((currentState) => ({
							...currentState,
							game: {
								...currentState.game,
								hasWon: true
							}
						}));
					}, 250);
				} else {
					setTimeout(() => {
						chess.move(state.game.moves[state.game.moveIndex]);
						currentGame.update((currentState) => ({
							...currentState,
							game: {
								...currentState.game,
								fen: playMove(state.game.fen, state.game.moves[state.game.moveIndex]),
								moveIndex: currentState.game.moveIndex + 1
							}
						}));
					}, 500);
				}
			}
		}
	});

	jump.subscribe((state) => {
		if (isInJump()) {
			isJumping = true;
			switch (state.action) {
				case 'init': {
					initJump();
					break;
				}
				case 'redo': {
					redoJump();
					break;
				}
				case 'undo': {
					undoJump();
					break;
				}
				case 'teardown': {
					chess.load(get(currentGame).game.fen);
				}
			}
		} else {
			isJumping = false;
		}
	});

	function moveListener(event: CustomEvent) {
		if (!isPlayerMove() || isJumping) {
			return;
		}
		const move = event.detail;

		if (!isCorrectMove(move.san)) {
			setTimeout(() => {
				chess.undo();
			}, 250);
		} else {
			currentGame.update((currentState) => ({
				...currentState,
				game: {
					...currentState.game,
					fen: move.after,
					moveIndex: currentState.game.moveIndex + 1
				}
			}));
		}
	}
</script>

<div
	class={`transition ${
		$currentGame.game.fen === '' || isJumping
			? 'grayscale-[0.75]'
			: $currentGame.game.hasWon
				? 'saturate-200'
				: ''
	}`}
>
	<Chess bind:this={chess} bind:orientation on:move={moveListener}></Chess>
</div>

<style>
</style>
