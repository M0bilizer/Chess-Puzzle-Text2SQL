<script lang="ts">
	import { currentPuzzles } from '$lib/stores/puzzleStore';

	currentPuzzles.subscribe((state) => {
		if (state.puzzles.length === 0) {
			const puzzle = state.puzzles[state.currentPuzzle];
		}
	});
</script>

{#if $currentPuzzles.puzzles.length !== 0}
	<div
		class="card h-max w-full max-w-md border-[1px] p-4 text-center border-surface-200-800 preset-filled-surface-100-900"
	>
		damn
	</div>
{/if}

<style>
</style>
