<script lang="ts">
	import TablerSearch from '~icons/tabler/search';
	import Spinner from '@/common/components/Spinner.svelte';

	type Props = {
		query: string;
		onSubmit: () => void;
		loading: boolean;
	};

	let { query = $bindable(), onSubmit, loading = $bindable() }: Props = $props();

	// Could consider using navigate instead
	function handleSubmit(event: SubmitEvent) {
		event.preventDefault();
		onSubmit();
	}
</script>

<search class="input-group flex w-full flex-row">
	<form onsubmit={handleSubmit} class="flex w-full">
		<input
			class="ig-input flex-1 px-6 text-xl break-normal disabled:cursor-progress"
			type="search"
			disabled={loading}
			placeholder="Find Dutch Defense Puzzle..."
			bind:value={query}
			aria-label="Search puzzles"
		/>
		<button
			type="submit"
			disabled={loading || !query.trim()}
			class="ig-btn inline-flex w-48 items-center gap-2 preset-filled px-4 py-2"
		>
			{#if loading}
				<Spinner />
			{:else}
				<TablerSearch class="size-4" />
			{/if}
			<span class="text-xl">{loading ? 'Searching...' : 'Search'}</span>
		</button>
	</form>
</search>
