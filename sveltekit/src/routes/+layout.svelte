<script lang="ts">
	import '../app.css';
	import Header from '$lib/layout/Header.svelte';
	import Footer from '$lib/layout/Footer.svelte';
	import Body from '$lib/layout/Body.svelte';
	let { children } = $props();
</script>

<Header />
<Body {children} />
<Footer />


