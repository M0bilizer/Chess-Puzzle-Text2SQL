<script lang="ts">
	import { Searches } from '$lib/stores/searchesStore';
	import SearchRecord from '$lib/components/layout/SearchRecord.svelte';
	import ClearButton from '$lib/components/ClearButton.svelte';
</script>

<div
	class="card flex min-w-0 flex-col gap-2 border-[1px] p-4 text-center shadow-xl border-surface-200-800 preset-filled-surface-100-900"
>
	<h4 class="h4">Recent {$Searches.size === 1 ? 'Search' : 'Searches'}</h4>
	<hr class="hr" />
	<div class="overflow-auto">
		{#if $Searches.size}
			<div class="flex flex-col gap-2">
				{#each [...$Searches.keys()] as key}
					<SearchRecord {key} />
				{/each}
			</div>
		{:else}
			<p class="type-subtitle">No recent searches</p>
		{/if}
	</div>
	{#if $Searches.size}
		<div class="flex w-full justify-end">
			<ClearButton />
		</div>
	{/if}
</div>
