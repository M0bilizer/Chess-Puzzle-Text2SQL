<script lang="ts">
	import { currentPuzzles } from '$lib/stores/puzzleStore';
	import { Chess } from 'svelte-chess';
	import type { Puzzle } from '$lib/types/puzzle';
	import { getActiveColor } from '$lib/utils/chessUtils';

	let puzzles: Puzzle[] = [];

	let chess;
	let orientation: 'w' | 'b' = 'w';
	let fen: string;
	currentPuzzles.subscribe((state) => {
		if (state.puzzles.length !== 0) {
			const puzzle = state.puzzles[state.currentPuzzle];
			chess.load(puzzle.fen);
			orientation = getActiveColor(puzzle.fen);
		}
	});
</script>

<div>
	<Chess bind:this={chess} {fen} {orientation} />
</div>

<style>
</style>
