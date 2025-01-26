<script lang="ts">
	import { currentGame } from '$lib/stores/currentGameStore';

	let puzzleId: string;
	let rating: number;
	let nbPlays: number;
	currentGame.subscribe((state) => {
		if (state.list.length) {
			const puzzle = state.list[state.index].puzzle;
			puzzleId = puzzle.puzzleId;
			rating = puzzle.rating;
			nbPlays = puzzle.nbPlays;
		}
	});
</script>

<div
	class="card h-max w-full max-w-md border-[1px] p-4 text-center shadow-xl border-surface-200-800 preset-filled-surface-100-900"
>
	<ul class="list-inside list-none space-y-1">
		<li class="text-left">Puzzle: #{puzzleId}</li>
		<li class="text-left">Rating: <b>{rating}</b></li>
		<li class="text-left">Played <b>{nbPlays}</b> times</li>
	</ul>
</div>

<style>
</style>
