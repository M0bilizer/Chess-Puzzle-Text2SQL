<script lang="ts">
	import Fa6SolidCheck from 'virtual:icons/fa6-solid/check';
	import { loadGame } from '$lib/utils/storeUtils';
	import { GameChipType } from '$lib/components/layout/GameChipType';
	import { Tooltip } from '@skeletonlabs/skeleton-svelte';
	import { onMount } from 'svelte';
	import { currentGame } from '$lib/stores/currentGameStore';
	import { Chessground } from 'svelte-chessground';
	import { get } from 'svelte/store';
	import { getFirstMoveColor } from '$lib/utils/chessUtils';

	let openState = $state(false);
	let { type, index } = $props();

	let fen: string = $state('');
	let orientation: 'white' | 'black' = $state('white');

	onMount(() => {
		const game = get(currentGame).list[index];
		fen = game.puzzle.fen;
		orientation = getFirstMoveColor(fen) === 'w' ? 'black' : 'white';
	});

	function handleClick() {
		loadGame(index);
	}
</script>

<Tooltip
	bind:open={openState}
	positioning={{ placement: 'top' }}
	contentBase="z-[1000]"
	openDelay={0}
	closeDelay={0}
>
	{#snippet trigger()}
		{#if type === GameChipType.won}
			<button type="button" onclick={handleClick} class="chip preset-filled-primary-500">
				<Fa6SolidCheck />
			</button>
		{:else if type === GameChipType.progress}
			<button type="button" onclick={handleClick} class="chip preset-filled-primary-500">
				<Fa6SolidCheck class="text-primary-500" />
			</button>
		{:else if type === GameChipType.locked}
			<button type="button" onclick={handleClick} class="chip preset-filled-secondary-500">
				<Fa6SolidCheck class="text-secondary-500" />
			</button>
		{/if}
	{/snippet}
	{#snippet content()}
		{#if type !== GameChipType.locked}
			<div
				class="card h-44 w-44 border-[1px] p-1 border-surface-200-800 preset-filled-surface-100-900"
			>
				<Chessground {fen} {orientation} />
			</div>
		{/if}
	{/snippet}
</Tooltip>
