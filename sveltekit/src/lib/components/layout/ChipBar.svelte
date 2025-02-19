<script lang="ts">
	import { currentGame, getNextGameIndex, isAllFinished } from '$lib/stores/currentGameStore';
	import GameChip from '$lib/components/ui/GameChip.svelte';
	import { GameChipType } from '$lib/types/GameChipType';

	let types: GameChipType[];
	currentGame.subscribe((state) => {
		if (isAllFinished()) {
			types = new Array(state.list.length).fill(GameChipType.won);
		} else {
			const current = getNextGameIndex();
			types = state.list.map((instance, index) => {
				if (index < current) {
					return GameChipType.won;
				} else if (index === current) {
					return GameChipType.progress;
				} else {
					return GameChipType.locked;
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
