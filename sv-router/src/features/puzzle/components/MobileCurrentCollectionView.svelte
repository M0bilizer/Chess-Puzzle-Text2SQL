<script lang="ts">
	import SvelteVirtualList from '@humanspeak/svelte-virtual-list';
	import { Progress } from '@skeletonlabs/skeleton-svelte';

	import { currentCollection } from '../store/current-collection.svelte';

	type Props = {
		currentId: string;
		class?: string;
	};

	let { class: className, currentId }: Props = $props();

	let solved = $derived(currentCollection.puzzles.filter((p) => p.result).length);

	let listRef: SvelteVirtualList | undefined = $state();
	export async function scrollToCurrent() {
		// put an wait so onMount can work properly
		await new Promise((resolve) => setTimeout(resolve, 1));
		const index = currentCollection.puzzles.findIndex((p) => p.puzzleId === currentId);
		if (index === -1) return;
		listRef?.scroll({ index: index, smoothScroll: true, align: 'top' });
	}
</script>

<section class="preset-filled-surface-100-900 p-4 {className}">
	<header>
		<p class="text-surface-400-600 text-xs">This collection:</p>
		<h2 class="preset-typo-subtitle inline">{currentCollection.name}</h2>
	</header>
	<hr class="hr my-2" />

	<Progress value={solved} class="grid grid-cols-[auto_auto_1fr_auto] items-center gap-4 px-2">
		<div>{solved}/{currentCollection.totalPuzzles} Completed</div>
		<Progress.Track>
			<Progress.Range />
		</Progress.Track>
	</Progress>
</section>
