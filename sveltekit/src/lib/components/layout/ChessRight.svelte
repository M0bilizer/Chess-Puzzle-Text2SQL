<script lang="ts">
	import { puzzleList } from '$lib/stores/puzzleStore';
	import PuzzleDescription from '$lib/components/layout/PuzzleDescription.svelte';
	import ThemesDescription from '$lib/components/layout/ThemesDescription.svelte';
	import NextButton from '$lib/components/layout/NextButton.svelte';
	import ThemesOrOpening from '$lib/components/layout/ThemesOrOpening.svelte';

	puzzleList.subscribe((state) => {
		if (state.puzzles.length !== 0) {
			const puzzle = state.puzzles[state.currentPuzzle];
		}
	});
</script>

{#if $puzzleList.puzzles.length !== 0}
	<div class="flex flex-col gap-5 px-2">
		<PuzzleDescription />
		<ThemesOrOpening />
		<NextButton />
	</div>
{/if}

<style>
</style>
