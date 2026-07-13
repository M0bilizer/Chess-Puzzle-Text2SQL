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
		display: flex;
		flex-direction: column;
		width: 100%;
		min-height: calc(100vh - 85px);
		gap: 0rem;
		margin: 0 auto;
	}

	/* Mobile: main takes full width */
	main > :global(section) {
		width: 100%;
		max-width: 100%;
	}

	/* Mobile: aside takes full width */
	main > :global(section + aside) {
		width: 100%;
		max-width: 100%;
		flex-shrink: 0;
	}

	/* lg */
	@media (min-width: 1024px) {
		main {
			flex-direction: row;
			gap: 2rem;
			justify-content: center;
		}

		/* Desktop: main fixed at 700px max */
		main > :global(section) {
			max-width: 700px;
			width: 100%;
			flex: 1;
		}

		/* Desktop: aside fixed at 330px */
		main > :global(section + aside) {
			width: 330px;
			max-width: 330px;
			flex-shrink: 0;
		}
	}
</style>
