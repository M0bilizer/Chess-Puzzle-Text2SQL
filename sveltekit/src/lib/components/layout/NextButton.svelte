<script lang="ts">
	import Fa6SolidSquareCaretRightRight from 'virtual:icons/fa6-solid/square-caret-right';
	import { gameState } from '$lib/stores/puzzleStore';

	let hasWon = false;
	gameState.subscribe((state) => {
		hasWon = state.hasWon;
	});
</script>

<div
	class="card grid h-max w-full max-w-md grid-rows-[auto_1fr_auto] border-[1px] shadow-xl border-surface-200-800 preset-filled-surface-100-900"
>
	{#if hasWon}
		<div class="h-20"></div>
		<div
			class="flex flex-row items-center gap-2 py-10 pl-2 preset-filled-primary-100-900 hover:preset-filled-primary-200-800"
		>
			<Fa6SolidSquareCaretRightRight class="size-16 text-tertiary-500" />
			<h3 class="h3">Next Puzzle</h3>
		</div>
		<div class="h-20"></div>
	{:else}
		<div class="h-20"></div>
		<div class="h-20"></div>
	{/if}
</div>

<style>
</style>
