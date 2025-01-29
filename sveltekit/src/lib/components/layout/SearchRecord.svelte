<script lang="ts">
	import { loadFromSearchRecord } from '$lib/utils/storeUtils';
	import SearchRecordToolTip from '$lib/components/modals/SearchRecordToolTip.svelte';

	let { key } = $props();

	function handleClick() {
		loadFromSearchRecord(key);
	}
</script>

<button class="px-2 text-left text-sm hover:preset-tonal-primary" onclick={handleClick}>
	<span class="flex flex-row justify-between">
		<span class="truncate text-surface-500">{key}</span>
		<span>
			<SearchRecordToolTip {key} type={'won'} />
			<SearchRecordToolTip {key} type={'notWon'} />
		</span>
	</span>
</button>

<style>
</style>
