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
		width: auto;
		max-width: 900px;
		height: 100%;
		margin: 0 auto;
		min-height: calc(100vh - 85px);
		padding: 48px 16px 0px 16px;
		overflow: hidden;
	}
</style>
