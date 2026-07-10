<script lang="ts">
	import ErrorAlert from '@/common/components/ErrorAlert.svelte';
	import WideMainOnlyPage from '@/common/components/WideMainOnlyPage.svelte';
	import SearchBanner from '@/features/puzzle/components/SearchBanner.svelte';
	import SearchForm from '@/features/puzzle/components/SearchForm.svelte';
	import { searchPuzzle } from '../api/puzzle.api';
	import { navigate } from '@/router';
	import { currentPlaylist } from '../store/current-playlist.svelte';
	import { getPlayerColor, getStartingFen } from '../utils';

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

<WideMainOnlyPage>
	<main class="space-y-12 pt-12">
		<section class="mx-auto space-y-12 px-4 lg:w-[900px] lg:px-0">
			<SearchBanner />
			<SearchForm bind:query onSubmit={() => handleSearch(query)} bind:loading />
			{#if error}
				<ErrorAlert {error} title="Search Failed" onDismiss={dismissError} />
			{/if}
		</section>
	</main>
</WideMainOnlyPage>
