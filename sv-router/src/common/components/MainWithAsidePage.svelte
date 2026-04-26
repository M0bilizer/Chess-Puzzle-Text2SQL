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
		flex-direction: column;
		width: 100%;
		min-height: calc(100vh - 77px - 60px);
		gap: 1rem;
		margin: 0 auto;
	}

	.container > :global(main) {
		max-width: 900px;
		width: 100%;
	}

	/* aside after main */
	.container > :global(main + aside) {
		width: 330px;
		flex-shrink: 0;
	}

	@media (min-width: 768px) {
		.container {
			flex-direction: row;
			gap: 2rem;
			justify-content: center;
		}

		.container > :global(main) {
			flex: 1;
		}
	}

	/* Only apply flex-direction change if main + aside pattern exists */
	@media (min-width: 768px) {
		.container:has(> :global(main + aside)) {
			flex-direction: row;
		}
	}
</style>
