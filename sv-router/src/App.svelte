<script lang="ts">
	import '@/router';
	import { Router } from 'sv-router';

	const mode = localStorage.getItem('mode');
	document.documentElement.setAttribute('data-mode', mode === 'dark' || !mode ? 'dark' : 'light');
</script>

<svelte:head>
	<title>Chess Puzzle Search</title>
</svelte:head>

<Router />
