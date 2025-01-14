<script lang="ts">
	import '../app.css';
	import Header from '$lib/components/layout/Header.svelte';
	import Footer from '$lib/components/layout/Footer.svelte';
	import Body from '$lib/components/layout/Body.svelte';
	import { ToastProvider } from '@skeletonlabs/skeleton-svelte';

	let { children } = $props();
</script>

<ToastProvider
	groupBase="toast !z-[1000] absolute"
	toastBase="card border border-error-950 border-x-2 border-b-1 grid grid-cols-[1fr_auto] items-center"
>
	<Header />
	<Body {children} />
	<Footer />
</ToastProvider>
