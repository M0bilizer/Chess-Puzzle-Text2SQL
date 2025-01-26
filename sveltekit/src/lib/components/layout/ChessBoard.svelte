<script lang="ts">
	import { Chess } from 'svelte-chess';
	import { playMove } from '$lib/utils/chessUtils';
	import { currentGame, type currentGameState } from '$lib/stores/currentGameStore';

	let chess: Chess;
	let orientation: 'w' | 'b' = 'w';
	currentGame.subscribe((state: currentGameState) => {
		if (state.game.fen !== '' && state.game.hasWon == false) {
			chess.load(state.game.fen);
			if (orientation !== state.game.orientation) {
				chess.toggleOrientation();
			}
			if (state.game.moveIndex % 2 === 0) {
				if (state.game.moveIndex > state.game.moves.length - 1) {
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
		if ($currentGame.game.moveIndex % 2 === 0) {
			return;
		}
		const move = event.detail;

		if ($currentGame.game.moves[$currentGame.game.moveIndex] !== move.san) {
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
