<script lang="ts">
	import apiStore from '$lib/stores/apiStore';
	import { Chessground } from 'svelte-chessground';
	import type { PuzzleType } from '$lib/types/puzzle';

	let puzzles: PuzzleType[] = [];
	apiStore.subscribe((state) => {
		puzzles = state.puzzleData;
		console.log(state.puzzleData);
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
			<Chessground fen={puzzles[0].fen} />
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
