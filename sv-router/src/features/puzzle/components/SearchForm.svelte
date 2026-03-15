<script>
	import TablerSearch from '~icons/tabler/search';
	import { searchPuzzleApi } from '@/features/puzzle/api/search-puzzle.api.ts';
	import SvgSpinnersRingResize from '~icons/svg-spinners/ring-resize';

	let searchQuery = '';
	let loading;
	let error;
	let results = [];

	async function fetchResults() {
		loading = true;
		error = null;

		try {
			results = await searchPuzzleApi(searchQuery);
		} catch (e) {
			error = e.message;
			results = [];
		} finally {
			loading = false;
		}
	}
</script>

<search class="input-group flex w-4/5 flex-row">
	<input
		class="ig-input px-12 text-2xl break-normal disabled:cursor-progress"
		type="search"
		disabled={loading}
		placeholder="Find Dutch Defense Puzzle..."
		bind:value={searchQuery}
	/>
	<button
		onclick={fetchResults}
		disabled={loading}
		class="ig-btn inline-flex items-center gap-2 preset-filled px-12 py-8 disabled:cursor-progress"
	>
		{#if loading}
			<SvgSpinnersRingResize />
		{:else }
			<TablerSearch class="size-6" />
		{/if}
		<span class="text-2xl">Search</span>
	</button>
</search>