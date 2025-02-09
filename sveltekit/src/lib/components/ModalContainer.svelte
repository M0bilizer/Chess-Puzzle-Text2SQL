<script lang="ts">
	import SearchModal from '$lib/components/modals/SearchModal.svelte';
	import CongratulationModal from '$lib/components/modals/CongratulationModal.svelte';
	import { SvelteToast } from '@zerodevx/svelte-toast';

	const options = {};
</script>

<div>
	<SvelteToast target="root" {options} />
</div>
<div class="hidden">
	<SearchModal />
	<CongratulationModal />
</div>
