<script lang="ts">
	import PuzzleContent from './+PuzzleContent.svelte';
	import { navigate, route } from '@/router';
	import { getPuzzle } from '../api/puzzle.api';
	import PuzzleSkeleton from './+PuzzleSkeleton.svelte';
	import { currentPlaylist } from '../store/current-playlist.svelte';
	import MainWithAsidePage from '@/common/components/MainWithAsidePage.svelte';
	import { PuzzleGame } from '../type.svelte';
	import { resource } from 'runed';
	import CurrentPlaylistView from '../components/CurrentPlaylistView.svelte';

	let currentPlaylistViewEl: CurrentPlaylistView | undefined = $state();
	let content: PuzzleContent | undefined = $state();

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
			currentPlaylistViewEl?.scrollToCurrent();
		}
	});

	$effect(() => {
		if (game) {
			new Promise((resolve) => setTimeout(resolve, 100)).then(() => content?.startGame());
		}
	});

	const onComplete = () => {
		currentPlaylist.setCurrentPuzzleResult(true);
	};

	const onNext = () => {
		if (currentPlaylist) {
			currentPlaylist.incrementCurrentIndex();
			navigate(`/puzzle/:id`, { params: { id: currentPlaylist.currentPuzzle!.puzzleId } });
		}
	};
</script>

<MainWithAsidePage>
	<aside class="max-h-[calc(100vh-85px)] overflow-auto">
		<CurrentPlaylistView bind:this={currentPlaylistViewEl} />
	</aside>

	{#if puzzleResource.current === undefined}
		<PuzzleSkeleton />
	{:else if puzzleResource.error}
		<div>Error loading puzzle: {puzzleResource.error.message}</div>
	{:else if puzzleResource.current && game}
		{@const puzzle = puzzleResource.current}
		<PuzzleContent bind:this={content} {puzzle} {game} hasNext={currentPlaylist.hasNext} {onComplete} {onNext} />
	{/if}
</MainWithAsidePage>
