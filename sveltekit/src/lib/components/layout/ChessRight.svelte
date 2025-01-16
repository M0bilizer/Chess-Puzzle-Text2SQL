<script lang="ts">
	import { currentPuzzles } from '$lib/stores/puzzleStore';
	import PuzzleDescription from '$lib/components/layout/PuzzleDescription.svelte';
	import ThemesDescription from '$lib/components/layout/ThemesDescription.svelte';
	import NextButton from '$lib/components/layout/NextButton.svelte';

	currentPuzzles.subscribe((state) => {
		if (state.puzzles.length !== 0) {
			const puzzle = state.puzzles[state.currentPuzzle];
		}
	});
</script>

{#if $currentPuzzles.puzzles.length !== 0}
	<div class="flex flex-col gap-5 px-2">
		<PuzzleDescription />
		<ThemesDescription />
		<NextButton />
	</div>
{/if}

<style>
</style>
