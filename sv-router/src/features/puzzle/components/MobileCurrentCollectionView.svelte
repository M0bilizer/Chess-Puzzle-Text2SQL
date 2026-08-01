<script lang="ts">
	import { p } from '@/router';
	import { Progress } from '@skeletonlabs/skeleton-svelte';
	import TablerChevronLeft from '~icons/tabler/chevron-left';
	import TablerChevronRight from '~icons/tabler/chevron-right';

	import { currentCollection } from '../store/current-collection.svelte';

	type Props = {
		currentId: string;
		class?: string;
	};

	let { class: className, currentId }: Props = $props();

	let solved = $derived(currentCollection.puzzles.filter((p) => p.result).length);
	let prevId = $derived(currentCollection.prev(currentId)?.puzzleId);
	let nextId = $derived(currentCollection.next(currentId)?.puzzleId);
	let currentIndex = $derived(currentCollection.puzzles.findIndex((p) => p.puzzleId === currentId));
</script>

<section class="preset-filled-surface-100-900 p-4 {className}">
	<header>
		<span class="text-surface-400-600 text-xs">This collection:</span>
		<h2 class="preset-typo-subtitle inline">{currentCollection.name}</h2>
	</header>
	<div>
		<span class="text-surface-400-600 text-xs">This puzzle:</span>
		<span>{currentIndex + 1}.</span>
		<span class="badge preset-filled-primary-500"> #{currentId}</span>
	</div>
	<hr class="hr my-2" />
	<Progress value={solved} class="grid grid-cols-[auto_auto_1fr_auto] items-center gap-4 px-2">
		{#if prevId}
			<a
				href={p('/puzzle/:id', { params: { id: prevId } })}
				data-scroll-to-top="false"
				class="btn-icon btn m-1 -p-1"
			>
				<TablerChevronLeft /></a
			>
		{:else}
			<button class="btn-icon btn m-1 -p-1" disabled><TablerChevronLeft /></button>
		{/if}
		<div>{solved}/{currentCollection.totalPuzzles} Completed</div>
		<Progress.Track>
			<Progress.Range />
		</Progress.Track>
		{#if nextId}
			<a
				href={p('/puzzle/:id', { params: { id: nextId } })}
				data-scroll-to-top="false"
				class="btn-icon btn m-1 -p-1"
			>
				<TablerChevronRight /></a
			>
		{:else}
			<button class="btn-icon btn m-1 -p-1" disabled><TablerChevronRight /></button>
		{/if}
	</Progress>
</section>
