<script lang="ts">
	import { Router } from 'sv-router';
	import '@/router';

	const mode = localStorage.getItem('mode');
	document.documentElement.setAttribute('data-mode', mode === 'dark' || !mode ? 'dark' : 'light');
</script>

<Router />
