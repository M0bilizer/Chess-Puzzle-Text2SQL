<script lang="ts">
	import '../app.css';
	import Header from '$lib/components/layout/Header.svelte';
	import Footer from '$lib/components/layout/Footer.svelte';
	import Body from '$lib/components/layout/Body.svelte';
	let { children } = $props();
</script>

<Header />
<Body {children} />
<Footer />
