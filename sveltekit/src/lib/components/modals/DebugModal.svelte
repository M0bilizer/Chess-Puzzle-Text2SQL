<script lang="ts">
	import { Modal } from '@skeletonlabs/skeleton-svelte';
	import { currentGame } from '$lib/stores/currentGameStore';
	import { searches } from '$lib/stores/searchesStore';
	import { get } from 'svelte/store';

	let modalOpen = $state(false);

	let query = $state('');
	let model = $state('');
	let sql = $state('');

	currentGame.subscribe((state) => {
		/*
		 when searching for new game is success
		 load to current game first, then update searches
		 because of that, searches is still undefined when current game updates.
		 */
		if (state.query !== '') {
			const search = get(searches).get(state.query);
			if (search === undefined) return;
			console.log(search.metadata.model);
			query = search.metadata.query;
			model = search.metadata.model == null ? 'Local data' : search.metadata.model;
			sql = search.metadata.sql == null ? 'Not applicable' : formatSql(search.metadata.sql);
		}
	});

	function formatSql(sql: string) {
		sql = sql.replace(/(WHERE)/gi, '\n$1');
		sql = sql.replace(/(AND|OR)/gi, '\n  $1');
		return sql;
	}
</script>

<Modal
	bind:open={modalOpen}
	contentBase="bg-surface-50-950 rounded-container top-[10%] m-0 mx-auto max-h-[90%] w-full max-w-[90%] space-y-8 p-8 text-inherit shadow-2xl md:max-w-[75%] md:max-w-2xl lg:max-w-4xl"
	backdropBackground="bg-tertiary-500/25"
	backdropClasses="backdrop-blur-sm"
>
	{#snippet trigger()}<span class="text-sm text-primary-500 hover:text-primary-700 hover:underline"
			>Debug</span
		>
	{/snippet}
	{#snippet content()}
		<table class="table">
			<caption>Debug menu</caption>
			<tbody>
				<tr>
					<td>Query:</td>
					<td><span class="code">{query}</span></td>
				</tr>
				<tr>
					<td>Model Used:</td>
					<td><span class="code">{model}</span></td>
				</tr>
				<tr>
					<td>SQL Statement:</td>
					<td><pre class="pre">{sql}</pre></td>
				</tr>
			</tbody>
		</table>
	{/snippet}
</Modal>
