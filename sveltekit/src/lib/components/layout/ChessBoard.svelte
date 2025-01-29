<script lang="ts">
	import { Chess } from 'svelte-chess';
	import { playMove } from '$lib/utils/chessUtils';
	import { currentGame, type currentGameState } from '$lib/stores/currentGameStore';
	import { get } from 'svelte/store';

	let chess: Chess;
	let orientation: 'w' | 'b' = 'w';

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

	currentGame.subscribe((state: currentGameState) => {
		if (state.game.fen !== '' && state.game.hasWon == false) {
			loadGame(state);
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

	function moveListener(event: CustomEvent) {
		if (!isPlayerMove()) {
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

<div>
	<Chess bind:this={chess} bind:orientation on:move={moveListener} />
</div>

<style>
</style>
