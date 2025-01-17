<script lang="ts">
	import { gameState } from '$lib/stores/puzzleStore';
	import { Chess } from 'svelte-chess';

	let chess: Chess = Chess;
	let orientation: 'w' | 'b' = 'w';
	gameState.subscribe((state) => {
		if (state.fen !== '') {
			chess.load(state.fen);
			orientation = state.orientation;

			setTimeout(() => {
				chess.move(state.moves[0]);
				gameState.update((currentState) => ({
					...currentState,
					moveIndex: 1,
					fen: chess.fen()
				}));
			}, 500);
		}
	});
</script>

<div>
	<Chess bind:this={chess} {orientation} />
</div>

<style>
</style>
