<script lang="ts">
	import Fa6SolidTrashCan from 'virtual:icons/fa6-solid/trash-can';
	import { Tooltip } from '@skeletonlabs/skeleton-svelte';
	import { deleteAllSearchResult } from '$lib/stores/searchesStore';
	let openState = $state(false);

	function clearResult() {
		deleteAllSearchResult();
	}
</script>

<Tooltip
	bind:open={openState}
	positioning={{ placement: 'bottom-end' }}
	triggerBase="underline"
	contentBase="card border-[1px] border-surface-200-800 preset-filled-surface-100-900 shadow-xl p-1"
	openDelay={200}
	closeDelay={50}
>
	{#snippet trigger()}
		<button onclick={() => clearResult()}
			><Fa6SolidTrashCan class="size-4 text-surface-500" />
		</button>{/snippet}
	{#snippet content()}<span class="text-surface-500">Clear all search</span>{/snippet}
</Tooltip>

<style>
</style>
