<script lang="ts">
	import '../app.css';
	import Header from '$lib/components/layout/Header.svelte';
	import Footer from '$lib/components/layout/Footer.svelte';
	import ModalContainer from '$lib/components/ModalContainer.svelte';

	let { children } = $props();
</script>

<Header />
<ModalContainer />
{@render children()}
<Footer />
