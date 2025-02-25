<script lang="ts">
	import SearchModal from '$lib/components/modals/SearchModal.svelte';
	import CongratulationModal from '$lib/components/modals/CongratulationModal.svelte';
	import { SvelteToast } from '@zerodevx/svelte-toast';
</script>

<div>
	<SvelteToast target="root" />
</div>
<div class="hidden">
	<SearchModal />
	<CongratulationModal />
</div>
