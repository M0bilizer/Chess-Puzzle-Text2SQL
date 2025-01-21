<script lang="ts">
	import { gameState } from '$lib/stores/puzzleStore';
	import { Chess } from 'svelte-chess';
	import { playMove } from '$lib/utils/chessUtils';

	let chess: Chess = Chess;
	let orientation: 'w' | 'b' = 'w';
	gameState.subscribe((state) => {
		if (state.fen !== '' && state.hasWon == false) {
			chess.load(state.fen);
			orientation = state.orientation;
			console.log(orientation)

			if (state.moveIndex % 2 === 0) {
				if (state.moveIndex > state.moves.length - 1) {
					setTimeout(() => {
						gameState.update((currentState) => ({
							...currentState,
							hasWon: true
						}));
					}, 250);
				} else {
					setTimeout(() => {
						chess.move(state.moves[state.moveIndex]);
						gameState.update((currentState) => ({
							...currentState,
							moveIndex: currentState.moveIndex + 1,
							fen: playMove(state.fen, state.moves[state.moveIndex])
						}));
					}, 500);
				}
			}
		}
	});

	function moveListener(event) {
		if ($gameState.moveIndex % 2 === 0) {
			return;
		}
		const move = event.detail;

		if ($gameState.moves[$gameState.moveIndex] !== move.san) {
			setTimeout(() => {
				chess.undo();
			}, 250);
		} else {
			gameState.update((currentState) => ({
				...currentState,
				moveIndex: currentState.moveIndex + 1,
				fen: move.after
			}));
		}
	}
</script>

<div>
	<Chess bind:this={chess} {orientation} on:move={moveListener} />
</div>

<style>
</style>
