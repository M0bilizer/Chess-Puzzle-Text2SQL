<script lang="ts">
	import { p } from '@/router';
	import { Progress } from '@skeletonlabs/skeleton-svelte';
	import TablerChevronLeft from '~icons/tabler/chevron-left';
	import TablerChevronRight from '~icons/tabler/chevron-right';

	import { currentPlaylist } from '../store/current-playlist.svelte';

	type Props = {
		currentId: string;
		class?: string;
	};

	let { class: className, currentId }: Props = $props();

	let solved = $derived(currentPlaylist.puzzles.filter((p) => p.result).length);
	let prevId = $derived(currentPlaylist.getPrev(currentId)?.puzzleId);
	let nextId = $derived(currentPlaylist.getNext(currentId)?.puzzleId);
</script>

<div class="preset-filled-surface-100-900 p-4 {className}">
	<Progress value={solved} class="grid grid-cols-[auto_auto_1fr_auto_auto] items-center gap-4">
		{#if prevId}
			<a href={p('/puzzle/:id', { params: { id: prevId } })} class="btn-icon btn">
				<TablerChevronLeft /></a
			>
		{:else}
			<button class="btn-icon btn" disabled><TablerChevronLeft /></button>
		{/if}
		<div class="flex gap-2">
			<span class="badge preset-filled">
				#{currentPlaylist.currentPuzzle?.puzzleId}
			</span>
			<h2>{currentPlaylist.name}</h2>
		</div>
		<Progress.Track>
			<Progress.Range />
		</Progress.Track>
		<div>{solved}/{currentPlaylist.totalPuzzles} solved</div>
		{#if nextId}
			<a href={p('/puzzle/:id', { params: { id: nextId } })} class="btn-icon btn">
				<TablerChevronRight /></a
			>
		{:else}
			<button class="btn-icon btn" disabled><TablerChevronRight /></button>
		{/if}
	</Progress>
</div>
