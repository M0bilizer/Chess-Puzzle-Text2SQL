<script lang="ts">
	import Header from '@/common/components/Header.svelte';
	let { children } = $props();
</script>

<Header />
{@render children()}
