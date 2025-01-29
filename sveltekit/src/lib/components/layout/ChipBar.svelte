<script lang="ts">
	import { isAllFinished, currentGame, getNextGameIndex } from '$lib/stores/currentGameStore';
	import GameChip from '$lib/components/layout/GameChip.svelte';

	let types: string[];
	currentGame.subscribe((state) => {
		if (isAllFinished()) {
			types = new Array(state.list.length).fill('won');
		} else {
			const current = getNextGameIndex();
			types = state.list.map((instance, index) => {
				if (index < current) {
					return 'won';
				} else if (index === current) {
					return 'progress';
				} else {
					return 'locked';
				}
			});
		}
	});
</script>

<div class="flex w-full flex-row gap-2 overflow-auto">
	{#each types as type, index}
		<GameChip {type} {index} />
	{/each}
</div>

<style>
</style>
