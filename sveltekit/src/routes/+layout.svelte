<script lang="ts">
	import '../app.css';
	import Header from '$lib/components/layout/Header.svelte';
	import Footer from '$lib/components/layout/Footer.svelte';
	import Body from '$lib/components/layout/Body.svelte';
	import { ToastProvider } from '@skeletonlabs/skeleton-svelte';

	let { children } = $props();
</script>

<ToastProvider groupBase="z-[1000] absolute">
	<Header />
	<Body {children} />
	<Footer />
</ToastProvider>
