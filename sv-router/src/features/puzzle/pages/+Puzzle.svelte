<script lang="ts">
	import MainWithAsidePage from '@/common/components/MainWithAsidePage.svelte';
	import { navigate, route } from '@/router';
	import { resource } from 'runed';

	import { getPuzzle } from '../api/puzzle.api';
	import CurrentCollectionView from '../components/CurrentCollectionView.svelte';
	import { currentCollection } from '../store/current-collection.svelte';
	import { PuzzleGame } from '../type.svelte';
	import PuzzleContent from './+PuzzleContent.svelte';
	import PuzzleSkeleton from './+PuzzleSkeleton.svelte';

	let currentCollectionViewEl: CurrentCollectionView | undefined = $state();
	let content: PuzzleContent | undefined = $state();

	let id = $derived(route.params.id as string);
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
			currentCollectionViewEl?.scrollToCurrent();
		}
	});

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
	<aside class="hidden max-h-[calc(100vh-85px)] overflow-auto lg:block">
		<CurrentCollectionView bind:this={currentCollectionViewEl} currentId={id} />
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
			hasNext={currentCollection.next(id) !== undefined}
			{onComplete}
			{onNext}
		/>
	{/if}
</MainWithAsidePage>
