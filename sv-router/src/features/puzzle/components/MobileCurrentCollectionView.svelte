<script lang="ts">
	import { p } from '@/router';
	import { Progress } from '@skeletonlabs/skeleton-svelte';
	import TablerChevronLeft from '~icons/tabler/chevron-left';
	import TablerChevronRight from '~icons/tabler/chevron-right';
	import { currentPlaylist } from '../store/current-playlist.svelte';

	type Props = {
		class?: string;
	};

	let { class: className }: Props = $props();

	let solved = $derived(currentPlaylist.puzzles.filter((p) => p.result).length);
	let prev = $derived(currentPlaylist.puzzles[currentPlaylist.currentIndex - 1].puzzleId);
	let next = $derived(currentPlaylist.puzzles[currentPlaylist.currentIndex + 1].puzzleId);
</script>

<div class="preset-filled-surface-100-900 p-4 {className}">
	<Progress value={solved} class="grid grid-cols-[auto_auto_1fr_auto_auto] items-center gap-4">
		<a href={p('/puzzle/:id', { params: { id: prev } })} class="btn-icon btn">
			<TablerChevronLeft /></a
		>
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
		<a href={p('/puzzle/:id', { params: { id: next } })} class="btn-icon btn">
			<TablerChevronRight /></a
		>
	</Progress>
</div>
