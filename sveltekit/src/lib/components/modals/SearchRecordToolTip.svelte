<script lang="ts">
	import { Searches } from '$lib/stores/searchesStore';
	import { Tooltip } from '@skeletonlabs/skeleton-svelte';

	let openState = $state(false);
	let { key, type } = $props();

	let number = $state(0);
	Searches.subscribe((state) => {
		const list = state.get(key);
		if (list === undefined) {
			number = -2;
			return;
		}
		if (type === 'won')
			number = list.reduce((sum, instance) => sum + (instance.progress.hasWon ? 1 : 0), 0);
		else if (type === 'notWon')
			number = list.reduce((sum, instance) => sum + (instance.progress.hasWon ? 0 : 1), 0);
		else number = -1;
	});
</script>

<Tooltip
	bind:open={openState}
	positioning={{ placement: 'top' }}
	triggerBase="underline"
	contentBase={`card p-1 z-[1000]
    ${type === 'won' ? 'preset-filled-primary-500 border-[1px] border-white' : ''}
    ${type === 'notWon' ? 'preset-filled-surface-100-900 border-[1px] border-primary-500' : ''}`}
	openDelay={200}
>
	{#snippet trigger()}
		{#if type === 'won'}
			<span class="chip px-1 py-0 preset-filled-primary-500">{number}</span>
		{:else if type === 'notWon'}
			<span class="chip px-1 py-0 preset-outlined-primary-500">{number}</span>
		{/if}
	{/snippet}
	{#snippet content()}
		{#if type === 'won'}
			{number} {number === 1 ? 'game' : 'games'} won
		{:else if type === 'notWon'}
			{number} {number === 1 ? 'game' : 'games'} left
		{/if}
	{/snippet}
</Tooltip>

<style>
</style>
