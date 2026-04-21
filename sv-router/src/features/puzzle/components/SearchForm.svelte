<script>
	import TablerSearch from '~icons/tabler/search';
	import SvgSpinnersRingResize from '~icons/svg-spinners/ring-resize';
	import { createEventDispatcher } from 'svelte';

	export let loading = false;
	export let initialQuery = '';

	const dispatch = createEventDispatcher();
	let searchQuery = initialQuery;

	function handleSubmit(e) {
		e.preventDefault();
		if (!searchQuery.trim() || loading) return;
		dispatch('search', searchQuery);
	}
</script>

<search class="input-group flex w-full flex-row">
	<form on:submit={handleSubmit} class="flex w-full">
		<input
			class="ig-input flex-1 px-6 text-xl break-normal disabled:cursor-progress"
			type="search"
			disabled={loading}
			placeholder="Find Dutch Defense Puzzle..."
			bind:value={searchQuery}
			aria-label="Search puzzles"
		/>
		<button
			type="submit"
			disabled={loading || !searchQuery.trim()}
			class="ig-btn inline-flex w-48 items-center gap-2 preset-filled px-4 py-2 disabled:cursor-progress"
		>
			{#if loading}
				<SvgSpinnersRingResize />
			{:else}
				<TablerSearch class="size-4" />
			{/if}
			<span class="text-xl">{loading ? 'Searching...' : 'Search'}</span>
		</button>
	</form>
</search>
