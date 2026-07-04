<script lang="ts">
	import PuzzleContent from './+PuzzleContent.svelte';
	import { navigate, route } from '@/router';
	import { getPuzzle } from '../api/puzzle.api';
	import PuzzleSkeleton from './+PuzzleSkeleton.svelte';
	import { playlistStore } from '../store/playlist.store';
	import MainWithAsidePage from '@/common/components/MainWithAsidePage.svelte';
	import Playlist from '../components/Playlist.svelte';
	import { PuzzleGame } from '../type.svelte';
	import { onMount } from 'svelte';

	let currentId = $state('');
	let hasNext = $state(false);
	let playlist: Playlist;

	$effect(() => {
		const { id } = route.getParams('/puzzle/:id');
		if (id && id !== currentId) {
			currentId = id;
			hasNext = playlistStore.hasNext();
			playlist.scrollToCurrent();
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
	{#await getPuzzle(currentId)}
		<PuzzleSkeleton />
	{:then value}
		{@const puzzle = value.getOrThrow()}
		{@const game = new PuzzleGame(puzzle)}
		<PuzzleContent {puzzle} {game} {hasNext} {onComplete} {onNext} />
	{/await}
</MainWithAsidePage>
