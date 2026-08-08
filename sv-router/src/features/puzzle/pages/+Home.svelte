<script lang="ts">
	import ErrorAlert from '@/common/components/ErrorAlert.svelte';
	import SimplePage from '@/common/components/SimplePage.svelte';
	import SearchBanner from '@/features/puzzle/components/SearchBanner.svelte';
	import SearchForm from '@/features/puzzle/components/SearchForm.svelte';
	import { searchDb } from '@/main';
	import { navigate } from '@/router';
	import { del } from 'idb-keyval';

	import { searchPuzzle } from '../api/puzzle.api';
	import ContinueLink from '../components/ContinueLink.svelte';
	import SearchTagline from '../components/SearchTagline.svelte';
	import { collections } from '../store/collections.svelte';
	import { currentCollection } from '../store/current-collection.svelte';

	let query = $state('');
	let loading = $state(false);
	let error: string | null = $state(null);

	async function handleSearch(query: string): Promise<void> {
		loading = true;
		error = null;

		try {
			const [data, err] = await searchPuzzle(query).toTuple();
			if (err) {
				error = err.message;
				return;
			}
			if (data.length === 0) {
				throw new Error('No results found.');
			}
			currentCollection.init(query, data);
			navigate('/puzzle/:id', {
				params: {
					id: data![0].puzzleId
				}
			});
		} catch (e: Error | unknown) {
			await del(query, searchDb);
			error = e instanceof Error ? e.message : 'unknown error';
		} finally {
			loading = false;
		}
	}

	function dismissError() {
		error = null;
	}
</script>

<SimplePage class="px-4">
	<section class="mx-auto pt-32 md:w-[900px]">
		<SearchBanner class="py-2" />
		<SearchForm bind:query onSubmit={() => handleSearch(query)} bind:loading />
		<div class="flex flex-wrap gap-8 px-6 py-2">
			{#each Object.values(collections.all) as playlist (playlist.name)}
				<ContinueLink {playlist} />
			{/each}
		</div>
		{#if error}
			<ErrorAlert {error} title="Search Failed" onDismiss={dismissError} class="py-2" />
		{/if}
		<SearchTagline class="py-16" />
	</section>
</SimplePage>
