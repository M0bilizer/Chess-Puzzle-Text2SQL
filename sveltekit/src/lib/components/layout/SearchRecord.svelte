<script lang="ts">
	import { loadFromSearchRecord } from '$lib/utils/storeUtils';

	let { key } = $props();

	function handleClick() {
		loadFromSearchRecord(key);
	}
</script>

<button class="px-2 text-left text-sm hover:preset-tonal-primary" onclick={handleClick}>
	<span class="truncate text-surface-500">{key}</span>
</button>

<style>
</style>
