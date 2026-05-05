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
		min-height: calc(100vh - 85px);
		gap: 0rem;
		margin: 0 auto;
	}

	/* Mobile: main takes full width */
	.container > :global(main) {
		width: 100%;
		max-width: 100%;
	}

	/* Mobile: aside takes full width */
	.container > :global(main + aside) {
		width: 100%;
		max-width: 100%;
		flex-shrink: 0;
	}

	/* lg */
	@media (min-width: 1024px) {
		.container {
			flex-direction: row;
			gap: 2rem;
			justify-content: center;
		}

		/* Desktop: main fixed at 700px max */
		.container > :global(main) {
			max-width: 700px;
			width: 100%;
			flex: 1;
		}

		/* Desktop: aside fixed at 330px */
		.container > :global(main + aside) {
			width: 330px;
			max-width: 330px;
			flex-shrink: 0;
		}
	}
</style>
