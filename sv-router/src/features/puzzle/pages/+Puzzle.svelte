<script lang="ts">
	import PuzzleContent from './+PuzzleContent.svelte';
	import { navigate, route } from '@/router';
	import { getPuzzle } from '../api/puzzle.api';
	import PuzzleSkeleton from './+PuzzleSkeleton.svelte';
	import { playlistStore } from '../store/playlist.store';
	import MainWithAsidePage from '@/common/components/MainWithAsidePage.svelte';
	import Playlist from '../components/Playlist.svelte';
	import { PuzzleGame } from '../type.svelte';
	import { resource } from 'runed';

	let hasNext = $derived(playlistStore.hasNext());
	let playlist: Playlist;
	let content: PuzzleContent = $state();

	// Using resource from runed library
	const puzzleResource = resource(
		() => route.getParams('/puzzle/:id').id,
		async (id) => {
			const result = await getPuzzle(id);
			return result.getOrThrow();
		}
	);

	let game = $derived(puzzleResource.current ? new PuzzleGame(puzzleResource.current) : null);

	$effect(() => {
		if (route.getParams('/puzzle/:id').id) {
			playlist.scrollToCurrent();
		}
	});

	$effect(() => {
		if (game) {
			new Promise((resolve) => setTimeout(resolve, 100)).then(() => content.startGame());
		}
	});

	const onComplete = () => {
		playlistStore.setCurrentPuzzleResult(true);
	};

	const onNext = () => {
		if (hasNext) {
			playlistStore.incrementCurrentIndex();
			navigate(`/puzzle/:id`, { params: { id: playlistStore.getCurrent().puzzleId } });
		}
	};
</script>

<MainWithAsidePage>
	<aside class="max-h-[calc(100vh-85px)] overflow-auto">
		<Playlist bind:this={playlist} />
	</aside>

	{#if puzzleResource.current === undefined}
		<PuzzleSkeleton />
	{:else if puzzleResource.error}
		<div>Error loading puzzle: {puzzleResource.error.message}</div>
	{:else if puzzleResource.current && game}
		{@const puzzle = puzzleResource.current}
		<PuzzleContent bind:this={content} {puzzle} {game} {hasNext} {onComplete} {onNext} />
	{/if}
</MainWithAsidePage>
