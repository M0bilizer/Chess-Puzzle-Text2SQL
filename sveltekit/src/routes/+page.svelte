<script lang="ts">
	import apiStore from '$lib/stores/apiStore';
	import { Chess } from 'svelte-chess';
	import type { PuzzleType } from '$lib/types/puzzle';
	import { getActiveColor } from '$lib/utils/chessUtils';
	let puzzles: PuzzleType[] = [];

	let chess;
	let orientation: 'w' | 'b' = 'w';
	let fen: string;
	apiStore.subscribe((state) => {
		puzzles = state.puzzleData;
		if (puzzles.length !== 0) {
			chess?.load(puzzles[0].fen);
			orientation = getActiveColor(puzzles[0].fen);
		}
	});
</script>

<div
	class="container mx-auto grid grid-cols-1 gap-x-4 py-4 sm:grid-cols-[250px_minmax(0px,_1fr)_300px]"
>
	{#if puzzles.length !== 0}
		<aside
			class="card border-[1px] text-center border-surface-200-800 preset-filled-surface-100-900"
		>
			aside
		</aside>
		<main id="_top" class="flex-start flex flex-col">
			<Chess bind:this={chess} {fen} {orientation} />
		</main>
		<aside
			class="card border-[1px] text-center border-surface-200-800 preset-filled-surface-100-900"
		>
			right
		</aside>
	{:else}
		<div class="h-[1000px]"></div>
	{/if}
</div>

<style>
</style>
