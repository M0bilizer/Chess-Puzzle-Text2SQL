<script lang="ts">
	import { currentPuzzles } from '$lib/stores/puzzleStore';

	let puzzleId: string;
	let rating: number;
	let nbPlays: number;
	let themes: string[];
	currentPuzzles.subscribe((state) => {
		if (state.puzzles.length !== 0) {
			const puzzle = state.puzzles[state.currentPuzzle];
			puzzleId = puzzle.puzzleId;
			rating = puzzle.rating;
			nbPlays = puzzle.nbPlays;
			themes = puzzle.themes.split(' ');
		}
	});
</script>

{#if $currentPuzzles.puzzles.length !== 0}
	<div class="flex flex-col gap-5 px-2">
		<div
			class="card h-max w-full max-w-md border-[1px] p-4 text-center border-surface-200-800 preset-filled-surface-100-900"
		>
			<ul class="list-inside list-none space-y-1">
				<li class="text-left">Puzzle: #{puzzleId}</li>
				<li class="text-left">Rating: <b>{rating}</b></li>
				<li class="text-left">Played <b>{nbPlays}</b> times</li>
			</ul>
		</div>
		<div
			class="card h-max w-full max-w-md border-[1px] p-4 text-center border-surface-200-800 preset-filled-surface-100-900"
		>
			<h4 class="h4">Puzzle Themes</h4>
			<div class="flex flex-col gap-2">
				<p class="type-subtitle text-left text-xs">
					Fun patterns or ideas in Chess. Learning themes makes chess easier and more exciting!
				</p>
				<div class="w-11/12 mx-auto">
				<hr class="hr" />
				</div>
				<div>
					{#each themes as row}
						<div class="text-left text-sm px-2 hover:preset-tonal-primary">
							{row}</div>
						{/each}
				</div>
			</div>
		</div>
	</div>
{/if}

<style>
</style>
