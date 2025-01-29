<script lang="ts">
	import { currentGame } from '$lib/stores/currentGameStore';

	let openings: string[];
	currentGame.subscribe((state) => {
		if (state.list.length) {
			const puzzle = state.list[state.index].puzzle;
			openings = puzzle.openingTags.split(' ');
		}
	});
</script>

<div>
	<h4 class="h4">Openings tags</h4>
	<div class="flex flex-col gap-2">
		<p class="type-subtitle text-left text-xs">
			Inital stage of a chess game. Professional chess players spend years studying openings!
		</p>
		<div class="mx-auto w-11/12">
			<hr class="hr" />
		</div>
		<div class="h-14 overflow-auto">
			{#each openings as row}
				<div class="px-2 text-left text-sm hover:preset-tonal-primary">
					{row}
				</div>
			{/each}
		</div>
	</div>
</div>

<style>
</style>
