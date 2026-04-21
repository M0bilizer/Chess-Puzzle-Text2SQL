<script lang="ts">
	import SearchForm from '@/features/puzzle/components/SearchForm.svelte';
	import { searchPuzzleApi } from '@/features/puzzle/api/search-puzzle.api';
	import { onMount } from 'svelte';
	import SearchBanner from '@/features/puzzle/components/SearchBanner.svelte';
	import ErrorAlert from '@/common/components/ErrorAlert.svelte';
	import ChessSkeleton from '@/features/puzzle/components/ChessSkeleton.svelte';
	import WideMainOnlyPage from '@/common/components/WideMainOnlyPage.svelte';
	import type { Puzzle } from '@/features/puzzle/puzzle';
	import ChessCard from '@/features/puzzle/components/ChessCard.svelte';

	let searchQuery = '';
	let loading = false;
	let error: string | null = null;
	let results: Puzzle[] = [];

	async function handleSearch(query: string): Promise<void> {
		searchQuery = query;
		loading = true;
		error = null;

		try {
			const [data, err] = await searchPuzzleApi(query).toTuple();
			if (err) {
				error = err.message;
			}
			results = data!;
			// update search param
			const url = new URL(window.location);
			url.searchParams.set('q', query);
			window.history.pushState({}, '', url);
		} catch (e) {
			error = e.message;
			results = [];
		} finally {
			loading = false;
		}
	}

	function dismissError() {
		error = null;
	}

	onMount(() => {
		const urlParams = new URLSearchParams(window.location.search);
		const q = urlParams.get('q');
		if (q) {
			handleSearch(q);
		}
	});
</script>

<WideMainOnlyPage>
	<main class="space-y-12">
		<section class="mx-auto w-[900px] space-y-2">
			<SearchBanner />
			<SearchForm
				on:search={({ detail }) => handleSearch(detail)}
				{loading}
				initialQuery={searchQuery}
			/>
			{#if error}
				<ErrorAlert {error} title="Search Failed" onDismiss={dismissError} />
			{/if}
		</section>

		{#if loading}
			<div class="grid grid-cols-4 place-items-center gap-2">
				{#each Array(16) as _, i}
					<ChessSkeleton key={i} />
				{/each}
			</div>
		{:else if results.length > 0}
			<div class="grid grid-cols-4 place-items-center gap-2">
				{#each results as puzzle, i}
					<ChessCard key={puzzle.id} {puzzle} />
				{/each}
			</div>
		{:else if searchQuery}
			<div class="text-center text-gray-500">No puzzles found</div>
		{/if}
	</main>
</WideMainOnlyPage>
