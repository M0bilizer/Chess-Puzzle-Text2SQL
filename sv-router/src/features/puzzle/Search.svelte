<script>
	import { searchParams } from 'sv-router';
	import { onMount } from 'svelte';
	import ThreeColumnPage from '../../common/components/ThreeColumnPage.svelte';

	let value = searchParams.get('q') || '';
	let results = [];
	let loading = false;
	let error = null;

	async function fetchResults() {
		loading = true;
		error = null;

		try {
			await new Promise((resolve) => setTimeout(resolve, 5000));

			results = [
				{ id: 1, title: `Result 1 for "${value}"`, description: 'This is a simulated result' },
				{ id: 2, title: `Result 2 for "${value}"`, description: 'Another simulated result' },
				{ id: 3, title: `Result 3 for "${value}"`, description: 'Yet another simulated result' }
			];
		} catch (e) {
			error = e.message;
			results = [];
		} finally {
			loading = false;
		}
	}
	onMount(fetchResults);
</script>

<ThreeColumnPage>
	{#snippet left()}
		<div class="space-y-4">
			<h3 class="font-bold">Navigation</h3>
			<ul>
				<li>Link 1</li>
				<li>Link 2</li>
				<li>Link 3</li>
			</ul>
		</div>
	{/snippet}

	{#if loading}
		<div class="loading">
			<div class="spinner"></div>
			<span>Searching for "{value}"... (5 second delay)</span>
		</div>
	{:else if error}
		<div class="error">Error: {error}</div>
	{:else if results.length > 0}
		<div class="results">
			{#each results as result (result.id)}
				<div class="result">
					<h3>{result.title}</h3>
					<p>{result.description}</p>
				</div>
			{/each}
		</div>
	{:else if value}
		<div class="no-results">No results found for "{value}"</div>
	{:else}
		<div class="empty">Enter a search query</div>
	{/if}
</ThreeColumnPage>
