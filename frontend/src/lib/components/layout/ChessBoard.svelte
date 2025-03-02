<script lang="ts">
	import { Chess } from 'svelte-chess';
	import { playMove } from '$lib/utils/chessUtils';
	import { currentGame, type currentGameState } from '$lib/stores/currentGameStore';
	import { get } from 'svelte/store';
	import { isInJump, jump, jumpAction } from '$lib/stores/jumpStore';
	import { feedbackState, feedbackStore } from '$lib/stores/feedbackStore';
	import { playCaptureSound, playMoveSound } from '$lib/utils/soundUtil';

	let chess: Chess;
	let orientation: 'w' | 'b' = $state('w');
	let isJumping = $state(false);

	function loadGame(state: currentGameState) {
		chess?.load(state.game.fen);
		if (orientation !== state.game.orientation) {
			chess?.toggleOrientation();
		}
	}

	function isPlayerMove() {
		return get(currentGame).game.moveIndex % 2 !== 0;
	}

	function isCorrectMove(move: string) {
		return get(currentGame).game.moves[get(currentGame).game.moveIndex] === move;
	}

	function isLastMove() {
		return get(currentGame).game.moveIndex + 1 > get(currentGame).game.moves.length - 1;
	}

	function resetJump() {
		const currentGameStore: currentGameState = get(currentGame);
		chess?.load(currentGameStore.list[currentGameStore.index].puzzle.fen);
	}

	function undoJump() {
		const currentGameStore: currentGameState = get(currentGame);
		chess?.load(currentGameStore.list[currentGameStore.index].puzzle.fen);
		currentGameStore.game.moves.slice(0, get(jump).current + 1).forEach((move) => chess.move(move));
		chess?.undo();
	}

	function redoJump() {
		const currentGameStore: currentGameState = get(currentGame);
		chess?.load(currentGameStore.list[currentGameStore.index].puzzle.fen);
		currentGameStore.game.moves.slice(0, get(jump).current).forEach((move) => chess.move(move));
	}

	function endJump() {
		const currentGameStore: currentGameState = get(currentGame);
		chess?.load(currentGameStore.list[currentGameStore.index].puzzle.fen);
		currentGameStore.game.moves.slice(0, get(jump).current).forEach((move) => chess.move(move));
	}

	currentGame.subscribe((state: currentGameState) => {
		if (state.query === '') chess?.reset();
		if (state.game.fen !== '') loadGame(state);
		if (state.game.hasWon === false) {
			if (state.game.moveIndex == 0 || state.game.moveIndex == 1) {
				if (orientation == 'w') feedbackStore.set(feedbackState.white);
				else feedbackStore.set(feedbackState.black);
			}
			if (!isPlayerMove()) {
				if (!isLastMove()) {
					setTimeout(() => {
						chess?.move(state.game.moves[state.game.moveIndex]);
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
			switch (state.action as jumpAction) {
				case jumpAction.reset: {
					resetJump();
					break;
				}
				case jumpAction.undo: {
					undoJump();
					break;
				}
				case jumpAction.redo: {
					redoJump();
					break;
				}
				case jumpAction.end: {
					endJump();
					break;
				}
				default: {
					console.warn(`Unrecognized action: ${state.action}`);
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
			playMoveSound();
			feedbackStore.set(feedbackState.wrong);
			setTimeout(() => {
				chess.undo();
			}, 250);
		} else {
			// eslint-disable-next-line @typescript-eslint/no-unused-expressions
			move.captured ? playCaptureSound() : playMoveSound();
			currentGame.update((currentState) => {
				let hasWon: boolean;
				if (isLastMove()) {
					hasWon = true;
					feedbackStore.set(feedbackState.won);
				} else {
					hasWon = false;
					feedbackStore.set(feedbackState.correct);
				}
				return {
					...currentState,
					game: {
						...currentState.game,
						fen: move.after,
						moveIndex: currentState.game.moveIndex + 1,
						hasWon: hasWon
					}
				};
			});
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
