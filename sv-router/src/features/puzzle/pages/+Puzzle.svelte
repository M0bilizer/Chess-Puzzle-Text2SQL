<script lang="ts">
	import PuzzleContent from './+PuzzleContent.svelte';
	import { navigate, route } from '@/router';
	import { getPuzzle } from '../api/puzzle.api';
	import PuzzleSkeleton from './+PuzzleSkeleton.svelte';
	import { playlist } from '../store/playlist.svelte';
	import MainWithAsidePage from '@/common/components/MainWithAsidePage.svelte';
	import Playlist from '../components/Playlist.svelte';
	import { PuzzleGame } from '../type.svelte';
	import { resource } from 'runed';

	let playlistEl: Playlist | undefined = $state();
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
			playlistEl?.scrollToCurrent();
		}
	});

	$effect(() => {
		if (game) {
			new Promise((resolve) => setTimeout(resolve, 100)).then(() => content?.startGame());
		}
	});

	const onComplete = () => {
		playlist.setCurrentPuzzleResult(true);
	};

	const onNext = () => {
		if (playlist) {
			playlist.incrementCurrentIndex();
			navigate(`/puzzle/:id`, { params: { id: playlist.currentPuzzle!.puzzleId } });
		}
	};
</script>

<MainWithAsidePage>
	<aside class="max-h-[calc(100vh-85px)] overflow-auto">
		<Playlist bind:this={playlistEl} />
	</aside>

	{#if puzzleResource.current === undefined}
		<PuzzleSkeleton />
	{:else if puzzleResource.error}
		<div>Error loading puzzle: {puzzleResource.error.message}</div>
	{:else if puzzleResource.current && game}
		{@const puzzle = puzzleResource.current}
		<PuzzleContent bind:this={content} {puzzle} {game} hasNext={playlist.hasNext} {onComplete} {onNext} />
	{/if}
</MainWithAsidePage>
