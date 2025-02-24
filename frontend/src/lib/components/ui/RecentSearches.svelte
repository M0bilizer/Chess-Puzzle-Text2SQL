<script lang="ts">
	import { searches } from '$lib/stores/searchesStore';
	import SearchRecord from '$lib/components/ui/SearchRecord.svelte';
	import ClearButton from '$lib/components/ui/ClearButton.svelte';
</script>

<div
	class="card flex flex-col gap-2 border-[1px] p-4 text-center shadow-xl border-surface-200-800 preset-filled-surface-100-900"
>
	<h4 class="h4">Recent {$searches.size === 1 ? 'Search' : 'Searches'}</h4>
	<hr class="hr" />
	<div class="grid grid-cols-1 overflow-auto">
		{#if $searches.size}
			<div class="flex flex-col gap-2">
				{#each [...$searches.keys()] as key}
					<SearchRecord {key} />
				{/each}
			</div>
		{:else}
			<p class="type-subtitle">No recent searches</p>
		{/if}
	</div>
	{#if $searches.size}
		<div class="flex w-full justify-end">
			<ClearButton />
		</div>
	{/if}
</div>
