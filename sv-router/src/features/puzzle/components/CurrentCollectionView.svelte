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
	};
	let { currentId }: Props = $props();

	let listRef: SvelteVirtualList | undefined = $state();
	export async function scrollToCurrent() {
		// put an wait so onMount can work properly
		await new Promise((resolve) => setTimeout(resolve, 1));
		const index = currentCollection.puzzles.findIndex((p) => p.puzzleId === currentId);
		if (index === -1) return;
		listRef?.scroll({ index: index, smoothScroll: true, align: 'top' });
	}

	let solved = $derived(currentCollection.puzzles.filter((p) => p.result).length);
</script>

{#if currentCollection.isActive}
	<section
		class="flex h-full w-full max-w-md flex-col space-y-2 divide-y divide-surface-200-800 card preset-filled-surface-100-900 py-2"
	>
		<header class="flex flex-col shrink-0 px-2 pb-1">
			<span class="text-surface-400-600 text-xs">Current collection:</span>
			<h2 class="preset-typo-subtitle">
				{currentCollection.name}
			</h2>
		</header>
		<SvelteVirtualList
			items={currentCollection.puzzles}
			bind:this={listRef as SvelteVirtualList}
			containerClass="relative overflow-hidden w-[244px] h-full"
		>
			{#snippet renderItem(puzzle, index)}
				{@const result = puzzle.result}
				{@const isCurrent = puzzle.puzzleId == currentId}
				<a
					href={p('/puzzle/:id', { params: { id: puzzle.puzzleId } })}
					data-scroll-to-top="false"
					class={[
						'flex cursor-pointer flex-row items-center rounded-none px-0 py-1 group hover:bg-primary-50-950',
						{ 'bg-success-50-950/75': result === true && !isCurrent },
						{ 'bg-primary-50-950/75': isCurrent }
					]}
				>
					<div class="min-w-10 grid grid-rows-3 grow items-start justify-items-center">
						<span
							class="text-sm"
							class:line-through={result === true}
							class:text-success-950-50={result === true && !isCurrent}
							class:group-hover:text-primary-950-50={true}>&nbsp;{index + 1}.&nbsp;</span
						>
						<div class="self-center">
							{#if isCurrent}
								<TablerPlay class="text-primary-950-50" />
							{:else if result === true}
								<TablerCheck class="text-success-950-50 group-hover:text-primary-950-50" />
							{/if}
						</div>
						<div></div>
					</div>
					<div
						class="thumbnail mr-1 overflow-hidden rounded-sm"
						class:brightness-75={result === true && !isCurrent}
						class:group-hover:brightness-100={true}
					>
						<Chessground fen={puzzle.fen} orientation={puzzle.orientation} viewOnly={true} />
					</div>
				</a>
			{/snippet}
		</SvelteVirtualList>
		<footer class="px-2 py-1">
			<Progress value={solved} class="grid grid-cols-[auto_1fr] items-center gap-4">
				<Progress.Label>{solved}/{currentCollection.totalPuzzles} Completed</Progress.Label>
				<Progress.Track>
					<Progress.Range />
				</Progress.Track>
			</Progress>
		</footer>
	</section>
{/if}

<style>
	/* hide the coordinates */
	.thumbnail :global(.ranks),
	.thumbnail :global(.files) {
		visibility: hidden;
	}
</style>
