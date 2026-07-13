<script lang="ts">
	import type { Snippet } from 'svelte';

	type Props = {
		children?: Snippet;
		class?: string;
	};
	let { children, class: className }: Props = $props();
</script>

<main class={className}>
	{@render children?.()}
</main>

<style>
	main {
		width: 100%;
		min-height: calc(100vh - 85px);
		overflow: hidden;
		margin: 0 auto;
	}
</style>
