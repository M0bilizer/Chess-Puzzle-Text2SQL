<script lang="ts">
	import { currentGameProgress } from '$lib/stores/puzzleStore';
	import { Chess } from 'svelte-chess';
	import { playMove } from '$lib/utils/chessUtils';

	let chess: Chess;
	let orientation: 'w' | 'b' = 'w';
	currentGameProgress.subscribe((state) => {
		if (state.currentFen !== '' && state.hasWon == false) {
			chess.load(state.currentFen);
			if (orientation !== state.orientation) {
				chess.toggleOrientation();
			}
			if (state.moveIndex % 2 === 0) {
				if (state.moveIndex > state.moves.length - 1) {
					setTimeout(() => {
						currentGameProgress.update((currentState) => ({
							...currentState,
							hasWon: true
						}));
					}, 250);
				} else {
					setTimeout(() => {
						chess.move(state.moves[state.moveIndex]);
						currentGameProgress.update((currentState) => ({
							...currentState,
							moveIndex: currentState.moveIndex + 1,
							currentFen: playMove(state.currentFen, state.moves[state.moveIndex])
						}));
					}, 500);
				}
			}
		}
	});

	function moveListener(event: CustomEvent) {
		if ($currentGameProgress.moveIndex % 2 === 0) {
			return;
		}
		const move = event.detail;

		if ($currentGameProgress.moves[$currentGameProgress.moveIndex] !== move.san) {
			setTimeout(() => {
				chess.undo();
			}, 250);
		} else {
			currentGameProgress.update((currentState) => ({
				...currentState,
				moveIndex: currentState.moveIndex + 1,
				currentFen: move.after
			}));
		}
	}
</script>

<div>
	<Chess bind:this={chess} bind:orientation on:move={moveListener} />
</div>

<style>
</style>
