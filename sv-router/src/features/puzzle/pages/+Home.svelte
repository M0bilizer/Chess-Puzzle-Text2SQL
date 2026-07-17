<script lang="ts">
	import ErrorAlert from '@/common/components/ErrorAlert.svelte';
	import HomeHeader from '@/common/components/HomeHeader.svelte';
	import SimplePage from '@/common/components/SimplePage.svelte';
	import SearchBanner from '@/features/puzzle/components/SearchBanner.svelte';
	import SearchForm from '@/features/puzzle/components/SearchForm.svelte';
	import { navigate } from '@/router';

	import { searchPuzzle } from '../api/puzzle.api';
	import ContinueLink from '../components/ContinueLink.svelte';
	import SearchTagline from '../components/SearchTagline.svelte';
	import { currentPlaylist } from '../store/current-playlist.svelte';
	import { playlistCollection } from '../store/playlist-collection.svelte';

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
			currentPlaylist.init(query, data);
			navigate('/puzzle/:id', {
				params: {
					id: data![0].puzzleId
				}
			});
		} catch (e: Error | unknown) {
			error = e instanceof Error ? e.message : 'unknown error';
		} finally {
			loading = false;
		}
	}

	function dismissError() {
		error = null;
	}
</script>

<HomeHeader />
<SimplePage class="px-4">
	<section class="mx-auto pt-32 lg:w-[900px]">
		<SearchBanner class="py-2" />
		<SearchForm bind:query onSubmit={() => handleSearch(query)} bind:loading />
		<div class="flex flex-wrap gap-8 px-6 py-2">
			{#each Object.values(playlistCollection.all) as playlist (playlist.name)}
				<ContinueLink {playlist} />
			{/each}
		</div>
		{#if error}
			<ErrorAlert {error} title="Search Failed" onDismiss={dismissError} class="py-2" />
		{/if}
		<SearchTagline class="py-16" />
	</section>
</SimplePage>
