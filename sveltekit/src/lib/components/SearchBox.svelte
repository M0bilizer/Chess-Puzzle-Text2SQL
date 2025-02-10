<script lang="ts">
	import Fa6SolidMagnifyingGlass from 'virtual:icons/fa6-solid/magnifying-glass';

	import { openSearchModal } from '$lib/stores/modalStore';

	function handleOnClick() {
		openSearchModal();
	}
</script>

<button
	onclick={() => handleOnClick()}
	class="btn btn-lg flex flex-row items-center justify-center gap-2 preset-filled-primary-500"
>
	<Fa6SolidMagnifyingGlass class="size-6" />
	<span>Search...</span>
</button>
