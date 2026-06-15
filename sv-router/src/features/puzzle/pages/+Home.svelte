<script lang="ts">
	import SearchForm from '@/features/puzzle/components/SearchForm.svelte';
	import { onMount } from 'svelte';
	import SearchBanner from '@/features/puzzle/components/SearchBanner.svelte';
	import ErrorAlert from '@/common/components/ErrorAlert.svelte';
	import ChessSkeleton from '@/features/puzzle/components/ChessSkeleton.svelte';
	import WideMainOnlyPage from '@/common/components/WideMainOnlyPage.svelte';
	import ChessCard from '@/features/puzzle/components/ChessCard.svelte';
	import { searchParams } from 'sv-router';
	import type { Puzzle } from '../type.svelte';
	import { searchStore } from '../store/search.store';
	import { searchPuzzle } from '../api/puzzle.api';

	let query = $state((searchParams.get('q') as string) || '');
	let loading = $state(false);
	let error: string | null = $state(null);
	let results: Puzzle[] = $state([]);

	async function handleSearch(query: string): Promise<void> {
		loading = true;
		error = null;

		try {
			const [data, err] = await searchPuzzle(query).toTuple();
			if (err) {
				error = err.message;
			}
			results = data!;
			searchStore.set({ search: query, result: data! });
		} catch (e: Error | unknown) {
			error = e instanceof Error ? e.message : 'unknown error';
			results = [];
		} finally {
			loading = false;
		}
	}

	function dismissError() {
		error = null;
	}

	// Update URL when query changes
	$effect(() => {
		if (query) {
			searchParams.set('q', query);
		} else {
			searchParams.delete('q');
		}
	});

	onMount(() => {
		// Get from store first, to avoid loading
		const store = $searchStore;
		if (store.result.length > 0) {
			results = store.result;
			query = store.search;
			return;
		}
		const q = (searchParams.get('q') as string) || '';
		if (q !== '') {
			query = q;
			handleSearch(q);
		}
	});
</script>

<WideMainOnlyPage>
	<main class="space-y-12 pt-12">
		<section class="mx-auto space-y-12 px-4 lg:w-[900px] lg:px-0">
			<SearchBanner />
			<SearchForm bind:query onSubmit={() => handleSearch(query)} bind:loading />
			{#if error}
				<ErrorAlert {error} title="Search Failed" onDismiss={dismissError} />
			{/if}
		</section>

		{#if loading}
			<div class="grid grid-cols-2 place-items-center gap-2 lg:grid-cols-4">
				{#each Array(16) as _, i (i)}
					<ChessSkeleton />
				{/each}
			</div>
		{:else if results.length > 0}
			<div class="grid grid-cols-2 place-items-center gap-2 lg:grid-cols-4">
				{#each results as puzzle, _i (puzzle.puzzleId)}
					<ChessCard {puzzle} />
				{/each}
			</div>
		{:else if query}
			<div class="text-center text-gray-500">No puzzles found</div>
		{/if}
	</main>
</WideMainOnlyPage>
