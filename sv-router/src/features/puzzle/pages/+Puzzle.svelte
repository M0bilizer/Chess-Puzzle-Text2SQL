<script lang="ts">
	import PuzzleContent from './+PuzzleContent.svelte';
	import { route } from '@/router';
	import { getPuzzle } from '../api/puzzle.api';
	import PuzzleSkeleton from './+PuzzleSkeleton.svelte';
	import { currentPlaylistStore } from '../store/currentSession.store';

	const { id } = route.getParams('/puzzle/:id');
	const hasNext = currentPlaylistStore.hasNext();
</script>

{#await getPuzzle(id)}
	<PuzzleSkeleton />
{:then value}
	{@const puzzle = value.getOrThrow()}
	<PuzzleContent {puzzle} {hasNext} />
{/await}
