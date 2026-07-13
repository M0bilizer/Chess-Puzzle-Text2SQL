<script lang="ts">
	import TablerSearch from '~icons/tabler/search';
	import Spinner from '@/common/components/Spinner.svelte';

	type Props = {
		query: string;
		onSubmit: () => void;
		loading: boolean;
		class?: string;
	};

	let {
		query = $bindable(),
		onSubmit,
		loading = $bindable(),
		class: className = ''
	}: Props = $props();

	function handleSubmit(event: SubmitEvent) {
		event.preventDefault();
		onSubmit();
	}
</script>

<search class={`${className}`}>
	<form onsubmit={handleSubmit} class="space-y-1">
		<div class="input-group flex w-full">
			<input
				id="search"
				class="ig-input flex-1 px-6 text-xl break-normal disabled:cursor-progress"
				type="search"
				disabled={loading}
				bind:value={query}
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
		</div>
	</form>
</search>
