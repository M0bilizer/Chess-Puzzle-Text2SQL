<script lang="ts">
	import PuzzleContent from './+PuzzleContent.svelte';
	import { route } from '@/router';
	import { getPuzzle } from '../api/search-puzzle';
	import PuzzleSkeleton from './+PuzzleSkeleton.svelte';

	const { id } = route.getParams('/puzzle/:id');
</script>

{#await getPuzzle(id)}
	<PuzzleSkeleton />
{:then value}
	{@const puzzle = value.getOrThrow()}
	<PuzzleContent {puzzle} />
{/await}
