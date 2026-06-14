<script lang="ts">
	import PuzzleContent from './+PuzzleContent.svelte';
	import { route } from '@/router';
	import { getPuzzle } from '../api/puzzle.api';
	import PuzzleSkeleton from './+PuzzleSkeleton.svelte';
	import { httpClient } from '@/main';

	const { id } = route.getParams('/puzzle/:id');
</script>

{#await getPuzzle(httpClient, id)}
	<PuzzleSkeleton />
{:then value}
	{@const puzzle = value.getOrThrow()}
	<PuzzleContent {puzzle} />
{/await}
