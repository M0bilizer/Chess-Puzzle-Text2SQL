<script lang="ts">
	import { loadFromSearchRecord } from '$lib/utils/storeUtils';
	import SearchRecordToolTip from '$lib/components/modals/SearchRecordToolTip.svelte';
	import { SearchRecordToolTipType } from '$lib/components/modals/SearchRecordToolTipType';

	let { key } = $props();

	function handleClick() {
		loadFromSearchRecord(key);
	}
</script>

<button class="px-2 text-left text-sm hover:preset-tonal-primary" onclick={handleClick}>
	<span class="flex flex-row justify-between gap-2">
		<span class="truncate text-surface-500">{key}</span>
		<span class="flex flex-row gap-1">
			<SearchRecordToolTip {key} type={SearchRecordToolTipType.won} />
			<SearchRecordToolTip {key} type={SearchRecordToolTipType.notWon} />
		</span>
	</span>
</button>
