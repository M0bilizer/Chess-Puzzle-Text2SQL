<script lang="ts">
	import { puzzleList } from '$lib/stores/puzzleStore';

	let themes: string[];
	puzzleList.subscribe((state) => {
		if (state.puzzles.length) {
			const puzzle = state.puzzles[state.currentPuzzle];
			themes = puzzle.themes.split(' ');
		}
	});
</script>

<div>
	<h4 class="h4">Puzzle Themes</h4>
	<div class="flex flex-col gap-2">
		<p class="type-subtitle text-left text-xs">
			Fun patterns or ideas in Chess. Learning themes makes chess easier and more exciting!
		</p>
		<div class="mx-auto w-11/12">
			<hr class="hr" />
		</div>
		<div>
			{#each themes as row}
				<div class="px-2 text-left text-sm hover:preset-tonal-primary">
					{row}
				</div>
			{/each}
		</div>
	</div>
</div>

<style>
</style>
