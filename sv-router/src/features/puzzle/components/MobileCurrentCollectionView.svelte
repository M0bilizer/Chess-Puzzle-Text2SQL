<script lang="ts">
	import Link from '@/common/components/Link.svelte';
	import TooltipButton from '@/common/components/TooltipButton.svelte';
	import { ordinalSuffix } from '@/common/utils';
	import { p } from '@/router';
	import SvelteVirtualList from '@humanspeak/svelte-virtual-list';
	import { Accordion, Progress } from '@skeletonlabs/skeleton-svelte';
	import { Chessground } from 'svelte5-chessground';
	import ArrowBarDown from '~icons/tabler/arrow-bar-down';
	import ArrowBarToUp from '~icons/tabler/arrow-bar-to-up';
	import TablerCheck from '~icons/tabler/check';
	import ChevronLeft from '~icons/tabler/chevron-left';
	import ChevronRight from '~icons/tabler/chevron-right';
	import TablerPlay from '~icons/tabler/play';

	import { currentCollection } from '../store/current-collection.svelte';

	type Props = {
		currentId: string;
		open: boolean;
		class?: string;
	};

	let { class: className, open = $bindable(), currentId }: Props = $props();

	let value = open ? ['1'] : undefined;

	let listRef: SvelteVirtualList | undefined = $state();
	let solved = $derived(currentCollection.puzzles.filter((p) => p.result).length);
	let index = $derived(currentCollection.puzzles.findIndex((p) => p.puzzleId === currentId));

	export async function scrollToCurrent() {
		// put an wait so onMount can work properly
		await new Promise((resolve) => setTimeout(resolve, 1));
		if (index === -1) return;
		listRef?.scroll({ index, smoothScroll: true, align: 'center' });
	}

	let prev = $derived(currentCollection.prev(currentId)?.puzzleId);
	let next = $derived(currentCollection.next(currentId)?.puzzleId);
</script>

<section class={className}>
	<Accordion
		{value}
		onValueChange={(value) => (open = value !== undefined)}
		class="preset-filled-surface-100-900 rounded-t-2xl"
		collapsible
	>
		<Accordion.Item value="1" class="gap-0">
			<header class="flex justify-between items-center p-2">
				<div>
					<p class="text-surface-400-600 text-xs">
						Playing the {index + 1}<sup>{ordinalSuffix(index + 1)}</sup> puzzle of this collection:
					</p>
					<h2 class="preset-typo-subtitle inline">{currentCollection.name}</h2>
				</div>
				<div class="flex flex-row gap-2 items-center">
					<TooltipButton message={prev ? 'Previous puzzle' : 'No previous puzzle'}>
						{#snippet button()}
							<Link
								href={prev ? p('/puzzle/:id', { params: { id: prev } }) : undefined}
								class="btn-icon btn-icon-lg hover:bg-surface-300-700"
							>
								<ChevronLeft />
							</Link>
						{/snippet}
					</TooltipButton>
					<TooltipButton message={next ? 'Next puzzle' : 'No next puzzle'}>
						{#snippet button()}
							<Link
								href={next ? p('/puzzle/:id', { params: { id: next } }) : undefined}
								data-scroll-to-top="false"
								class="btn-icon btn-icon-lg hover:bg-surface-300-700"
							>
								<ChevronRight />
							</Link>
						{/snippet}
					</TooltipButton>

					<Accordion.ItemIndicator class="group">
						<TooltipButton message={open ? 'Collapse' : 'Expand to view all puzzles'}>
							{#snippet button()}
								<Accordion.ItemTrigger class="btn-icon btn-icon-lg hover:bg-surface-300-700">
									<ArrowBarToUp class="hidden group-data-[state=open]:block" />
									<ArrowBarDown class="block group-data-[state=open]:hidden" />
								</Accordion.ItemTrigger>
							{/snippet}
						</TooltipButton>
					</Accordion.ItemIndicator>
				</div>
			</header>
			<Accordion.ItemContent class="py-0 px-1">
				<SvelteVirtualList
					bind:this={listRef as SvelteVirtualList}
					orientation="horizontal"
					items={currentCollection.puzzles}
					containerClass="relative overflow-hidden w-full h-[275px] bg-surface-50-950"
				>
					{#snippet renderItem(puzzle, index)}
						{@const result = puzzle.result}
						{@const isCurrent = puzzle.puzzleId == currentId}
						<a
							href={p('/puzzle/:id', { params: { id: puzzle.puzzleId } })}
							data-scroll-to-top="false"
							class={[
								'flex flex-col gap-2 cursor-pointer items-center py-2 px-1 m-2 group rounded-lg hover:bg-primary-50-950',
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
									class:group-hover:text-primary-950-50={true}
									>&nbsp;{Number(index) + 1}.&nbsp;</span
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
			</Accordion.ItemContent>
		</Accordion.Item>
	</Accordion>
	<Progress
		value={solved}
		class="grid grid-cols-[auto_1fr] items-center gap-4 py-2 px-1 bg-surface-100-900 rounded-b-none md:rounded-b-lg"
	>
		<div class="text-xs">{solved}/{currentCollection.totalPuzzles} Completed</div>
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
