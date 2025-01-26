<script lang="ts">
	import { currentGame } from '$lib/stores/currentGameStore';
	import GameChip from '$lib/components/layout/GameChip.svelte';

	let types: string[];
	currentGame.subscribe((state) => {
		const current = state.index;
		types = state.list.map((instance, index) => {
			if (instance.progress.hasWon === true) {
				return 'won';
			} else if (index === current) {
				return 'progress';
			} else {
				return 'locked';
			}
		});
		console.log(types);
	});
</script>

<div class="flex w-full flex-row gap-2 overflow-auto">
	{#each types as type}
		<GameChip {type} />
	{/each}
</div>

<style>
</style>
