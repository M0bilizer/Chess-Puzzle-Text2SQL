<script lang="ts">
	import MainWithAsidePage from '@/common/components/MainWithAsidePage.svelte';
	import { navigate, route } from '@/router';
	import { resource } from 'runed';

	import { getPuzzle } from '../api/puzzle.api';
	import CurrentPlaylistView from '../components/CurrentPlaylistView.svelte';
	import { currentPlaylist } from '../store/current-playlist.svelte';
	import { PuzzleGame } from '../type.svelte';
	import PuzzleContent from './+PuzzleContent.svelte';
	import PuzzleSkeleton from './+PuzzleSkeleton.svelte';

	let currentPlaylistViewEl: CurrentPlaylistView | undefined = $state();
	let content: PuzzleContent | undefined = $state();

	// Using resource from runed library
	let id = $derived(route.getParams('/puzzle/:id').id);
	const puzzleResource = resource(
		() => id,
		async (id) => {
			const result = await getPuzzle(id);
			return result.getOrThrow();
		}
	);

	let game = $derived(puzzleResource.current ? new PuzzleGame(puzzleResource.current) : null);

	$effect(() => {
		if (id) {
			currentPlaylistViewEl?.scrollToCurrent();
		}
	});

	$effect(() => {
		if (game) {
			new Promise((resolve) => setTimeout(resolve, 100)).then(() => content?.startGame());
		}
	});

	const onComplete = () => {
		currentPlaylist.setPuzzleResult(id, true);
	};

	const onNext = () => {
		if (currentPlaylist) {
			const result = currentPlaylist.getNextPuzzle();
			if (!result) {
				console.error('No next puzzle');
				return;
			}
			navigate(`/puzzle/:id`, { params: { id: result.puzzleId } });
		}
	};
</script>

<MainWithAsidePage class="px-1">
	<aside class="hidden max-h-[calc(100vh-85px)] overflow-auto lg:block">
		<CurrentPlaylistView bind:this={currentPlaylistViewEl} currentId={id} />
	</aside>

	{#if puzzleResource.current === undefined}
		<PuzzleSkeleton />
	{:else if puzzleResource.error}
		<div>Error loading puzzle: {puzzleResource.error.message}</div>
	{:else if puzzleResource.current && game}
		{@const puzzle = puzzleResource.current}
		<PuzzleContent
			bind:this={content}
			{puzzle}
			{game}
			hasNext={currentPlaylist.hasNext}
			{onComplete}
			{onNext}
		/>
	{/if}
</MainWithAsidePage>
