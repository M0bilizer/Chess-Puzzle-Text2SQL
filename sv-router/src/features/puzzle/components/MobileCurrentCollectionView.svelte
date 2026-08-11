<script lang="ts">
	import { p } from '@/router';
	import SvelteVirtualList from '@humanspeak/svelte-virtual-list';
	import { Progress } from '@skeletonlabs/skeleton-svelte';
	import { Chessground } from 'svelte5-chessground';
	import TablerCheck from '~icons/tabler/check';
	import TablerPlay from '~icons/tabler/play';

	import { currentCollection } from '../store/current-collection.svelte';

	type Props = {
		currentId: string;
		class?: string;
	};

	let { class: className, currentId }: Props = $props();

	let listRef: SvelteVirtualList | undefined = $state();
	let solved = $derived(currentCollection.puzzles.filter((p) => p.result).length);

	export async function scrollToCurrent() {
		// put an wait so onMount can work properly
		await new Promise((resolve) => setTimeout(resolve, 1));
		const index = currentCollection.puzzles.findIndex((p) => p.puzzleId === currentId);
		if (index === -1) return;
		listRef?.scroll({ index, smoothScroll: true, align: 'center' });
	}
</script>

<section class="preset-filled-surface-100-900 p-4 rounded {className}">
	<header>
		<p class="text-surface-400-600 text-xs">This collection:</p>
		<h2 class="preset-typo-subtitle inline">{currentCollection.name}</h2>
	</header>
	<hr class="hr my-2" />
	<SvelteVirtualList
		bind:this={listRef as SvelteVirtualList}
		orientation="horizontal"
		items={currentCollection.puzzles}
		containerClass="relative overflow-hidden w-full h-[275px]"
	>
		{#snippet renderItem(puzzle, index)}
			{@const result = puzzle.result}
			{@const isCurrent = puzzle.puzzleId == currentId}
			<a
				href={p('/puzzle/:id', { params: { id: puzzle.puzzleId } })}
				data-scroll-to-top="false"
				class={[
					'flex flex-col gap-2 cursor-pointer items-center py-2 px-1 m-2 group rounded-lg',
					{ 'border border-surface-200-800': result === false && !isCurrent },
					{ 'bg-success-50-950/75': result === true && !isCurrent },
					{ 'bg-primary-50-950/75': isCurrent }
				]}
			>
				<div
					class="thumbnail mr-1 overflow-hidden rounded-sm"
					class:brightness-75={result === true && !isCurrent}
					class:group-hover:brightness-100={true}
				>
					<Chessground fen={puzzle.fen} orientation={puzzle.orientation} viewOnly={true} />
				</div>

				<div class="flex flex-row gap-2 items-center">
					<span
						class="text-sm"
						class:line-through={result === true}
						class:text-success-950-50={result === true && !isCurrent}
						class:group-hover:text-primary-950-50={true}>&nbsp;{Number(index) + 1}.&nbsp;</span
					>
					{#if isCurrent}
						<TablerPlay class="text-primary-950-50" />
					{:else if result === true}
						<TablerCheck class="text-success-950-50 group-hover:text-primary-950-50" />
					{/if}
				</div>
			</a>
		{/snippet}
	</SvelteVirtualList>
	<hr class="hr my-2" />
	<Progress value={solved} class="grid grid-cols-[auto_auto_1fr_auto] items-center gap-4 px-2">
		<div>{solved}/{currentCollection.totalPuzzles} Completed</div>
		<Progress.Track>
			<Progress.Range />
		</Progress.Track>
	</Progress>
</section>

<style>
	/* hide the coordinates */
	.thumbnail :global(.ranks),
	.thumbnail :global(.files) {
		visibility: hidden;
	}
</style>
