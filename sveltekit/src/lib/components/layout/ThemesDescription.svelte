<script lang="ts">
	import { currentPuzzles } from '$lib/stores/puzzleStore';

	let themes: string[];
	currentPuzzles.subscribe((state) => {
		if (state.puzzles.length !== 0) {
			const puzzle = state.puzzles[state.currentPuzzle];
			themes = puzzle.themes.split(' ');
		}
	});
</script>

<div
	class="card h-max w-full max-w-md border-[1px] p-4 text-center shadow-xl border-surface-200-800 preset-filled-surface-100-900"
>
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
