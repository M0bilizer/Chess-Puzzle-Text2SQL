<script lang="ts">
	import { pastPuzzles } from '$lib/stores/puzzleStore';
	import ClearButton from '$lib/components/ClearButton.svelte';
</script>

<div
	class="card flex max-h-screen w-full max-w-md flex-col gap-2 overflow-auto border-[1px] p-4 text-center border-surface-200-800 preset-filled-surface-100-900"
>
	<h4 class="h4">Recent Search</h4>
	<hr class="hr" />
	<div>
		{#if $pastPuzzles.length}
			<div class="flex flex-col gap-2">
				{#each $pastPuzzles as row}
					<div class="px-2 text-left text-sm hover:preset-tonal-primary">
						<p class="text-surface-500 truncate">{row.query}</p>
					</div>
				{/each}
				<div class="flex w-full justify-end">
					<ClearButton />
				</div>
			</div>
		{:else}
			<p class="type-subtitle">No recent searches</p>
		{/if}
	</div>
</div>

<style>
</style>
