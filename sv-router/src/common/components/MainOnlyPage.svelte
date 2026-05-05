<script lang="ts">
	import type { Snippet } from 'svelte';

	type Props = {
		children?: Snippet;
		class?: string;
	};
	let { children, class: className }: Props = $props();
</script>

<div class={['container', className]}>
	{@render children?.()}
</div>

<style>
	.container {
		display: flex;
		justify-content: center;
		width: 100%;
		min-height: calc(100vh - 85px);
		overflow: hidden;
		margin: 0 auto;
	}

	.container > :global(main) {
		max-width: 900px;
		width: 100%;
		min-height: 100%;
	}
</style>
