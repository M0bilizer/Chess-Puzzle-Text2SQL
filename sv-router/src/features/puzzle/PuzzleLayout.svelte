<script lang="ts">
	import Appbar from '@/common/components/Appbar.svelte';
	import Footer from '@/common/components/Footer.svelte';

	let { children } = $props();
</script>

<Appbar />
{@render children()}
<!-- <Footer /> -->
