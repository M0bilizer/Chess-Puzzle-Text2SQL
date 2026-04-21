<script lang="ts">
	import Appbar from '@/common/components/Appbar.svelte';

	let { children } = $props();
</script>

<Appbar />
{@render children()}