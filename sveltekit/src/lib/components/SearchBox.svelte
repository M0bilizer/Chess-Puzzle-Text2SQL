<script lang="ts">
	import SearchIcon from '$lib/components/icons/SearchIcon.svelte';

	import { searchModalState } from '$lib/stores/modalStore';

	function handleOnClick() {
		searchModalState.set({ open: true });
	}
</script>

<button
	onclick={() => handleOnClick()}
	class="btn btn-lg flex flex-row items-center justify-center gap-2 preset-filled-primary-500"
>
	<SearchIcon />
	<span>Search...</span>
</button>
