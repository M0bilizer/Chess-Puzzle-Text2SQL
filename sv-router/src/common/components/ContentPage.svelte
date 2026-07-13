<script lang="ts">
	import type { Snippet } from 'svelte';

	type Props = {
		children?: Snippet;
		class?: string;
	};
	let { children, class: className }: Props = $props();
</script>

<main>
	<article class={className}>
		{@render children?.()}
	</article>
</main>

<style>
	main {
		width: 100%;
		min-height: calc(100vh - 85px);
		overflow: hidden;
	}

	article {
		max-width: 900px;
		width: 100%;
		height: 100%;
		margin: 0 auto;
		padding: 48px 0px 0px 0px;
	}
</style>
