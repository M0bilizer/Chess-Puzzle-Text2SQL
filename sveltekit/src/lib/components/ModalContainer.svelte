<script lang="ts">
	import SearchModal from '$lib/components/modals/SearchModal.svelte';
	import CongratulationModal from '$lib/components/modals/CongratulationModal.svelte';
	import { Toaster } from 'svelte-sonner';
	import { searchModalState } from '$lib/stores/modalStore';
	let open = $state(false);

	searchModalState.subscribe((state) => {
		open = state.open;
	});
</script>

<div></div>
<div class="hidden">
	<SearchModal />
	<CongratulationModal />
</div>
