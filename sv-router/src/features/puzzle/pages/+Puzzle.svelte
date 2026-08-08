<script lang="ts">
	import MainWithAsidePage from '@/common/components/MainWithAsidePage.svelte';
	import { navigate, route } from '@/router';
	import { resource, watch } from 'runed';

	import { getPuzzle } from '../api/puzzle.api';
	import CurrentCollectionView from '../components/CurrentCollectionView.svelte';
	import MobileChessDescription from '../components/MobileChessDescription.svelte';
	import MobileCurrentCollectionView from '../components/MobileCurrentCollectionView.svelte';
	import { currentCollection } from '../store/current-collection.svelte';
	import { PuzzleGame } from '../type.svelte';
	import PuzzleContent from './+PuzzleContent.svelte';
	import PuzzleSkeleton from './+PuzzleSkeleton.svelte';

	let currentCollectionViewEl: CurrentCollectionView | undefined = $state();
	let mobileCollectionViewEl: MobileCurrentCollectionView | undefined = $state();
	let content: PuzzleContent | undefined = $state();
	let openDescription = $state(false);

	let { id } = $derived(route.getParams('/puzzle/:id'));
	const puzzleResource = resource(
		() => id,
		async (id) => {
			const result = await getPuzzle(id);
			return result.getOrThrow();
		}
	);

	let puzzle = $derived(puzzleResource.current);
	let game = $derived(puzzleResource.current ? new PuzzleGame(puzzleResource.current) : null);

	watch(
		() => id,
		(prevId, newId) => {
			if (prevId !== newId) {
				mobileCollectionViewEl?.scrollToCurrent();
				currentCollectionViewEl?.scrollToCurrent();
			}
		}
	);

	$effect(() => {
		if (game) {
			new Promise((resolve) => setTimeout(resolve, 100)).then(() => content?.startGame());
		}
	});

	const onComplete = () => {
		currentCollection.setPuzzleResult(id, true);
	};

	const onNext = () => {
		if (currentCollection) {
			const result = currentCollection.getFirstUnsolved();
			if (!result) {
				console.error('No next puzzle');
				return;
			}
			navigate(`/puzzle/:id`, { params: { id: result.puzzleId } });
		}
	};
</script>

<MainWithAsidePage>
	<aside class="order-last md:order-first space-y-1">
		<CurrentCollectionView
			bind:this={currentCollectionViewEl}
			currentId={id}
			class="hidden md:flex"
		/>
		{#if puzzle !== undefined}
			<MobileChessDescription bind:open={openDescription} {puzzle} class="block md:hidden" />
		{/if}
		<MobileCurrentCollectionView
			bind:this={mobileCollectionViewEl}
			currentId={id}
			class="block md:hidden"
		/>
	</aside>

	{#if puzzle === undefined}
		<PuzzleSkeleton />
	{:else if puzzleResource.error}
		<div>Error loading puzzle: {puzzleResource.error.message}</div>
	{:else if puzzleResource.current && game}
		<PuzzleContent
			bind:this={content}
			{puzzle}
			{game}
			bind:openDescription
			hasNext={currentCollection.next(id) !== undefined}
			{onComplete}
			{onNext}
		/>
	{/if}
</MainWithAsidePage>
