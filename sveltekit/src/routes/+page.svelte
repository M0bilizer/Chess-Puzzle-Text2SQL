<script lang="ts">
	import { fetchSQLData, fetchLLMData } from '$lib/utils/api';
	import ErrorBox from '$lib/components/ErrorBox.svelte';
	import type { PuzzleType } from '$lib/types/puzzle';

	let SQLValue = $state('');
	let SQLResponse = $state<PuzzleType[]>([]);
	let SQLError = $state('');

	let LLMValue = $state('');
	let LLMResponse = $state('');
	let LLMError = $state('');

	// Event handlers
	const handleSQLSubmit = async (event: Event) => {
		event.preventDefault();

		try {
			const data = await fetchSQLData(SQLValue);
			SQLError = '';
			SQLResponse = data;
		} catch (error) {
			SQLResponse = [];
			if (error instanceof Error) {
				SQLError = error.message;
			} else {
				SQLError = 'An unexpected error occurred';
			}
		} finally {
			SQLValue = '';
		}
	};

	const handleLLMSubmit = async (event: Event) => {
		event.preventDefault();

		try {
			const data = await fetchLLMData(LLMValue);
			LLMError = '';
			LLMResponse = data;
		} catch (error) {
			if (error instanceof Error) {
				LLMError = error.message;
			} else {
				LLMError = 'An unexpected error occurred';
			}
		} finally {
			LLMValue = '';
		}
	};
</script>

<div class="ml-auto mr-auto flex h-full w-1/3 flex-col items-center gap-4 rounded-lg pl-4 pt-40">
	<h1>Chess Puzzle</h1>
	<div class="flex gap-4 border border-gray-200 p-4">
		<form onsubmit={handleSQLSubmit} class="flex flex-col gap-5">
			<label for="sql">SQL</label>
			<input id="sql" type="text" bind:value={SQLValue} class="border" />
			<button class="rounded border" type="submit">Submit</button>
		</form>
		<div class="flex flex-col border border-gray-200 p-2">
			Result
			{#each SQLResponse as puzzle (puzzle.id)}
				<li>{puzzle.puzzleId}</li>
			{/each}
			{#if SQLError}
				<ErrorBox error={SQLError} />
			{/if}
		</div>
	</div>

	<div class="flex gap-4 border border-gray-200 p-4">
		<form onsubmit={handleLLMSubmit} class="flex flex-col gap-5">
			<label for="LLM">LLM</label>
			<input id="llm" type="text" bind:value={LLMValue} class="border" />
			<button class="rounded border" type="submit">Submit</button>
		</form>
		<p class="flex flex-col border border-gray-200 p-2">
			Result
			{LLMResponse}
			{#if LLMError}
				<ErrorBox error={LLMError} />
			{/if}
		</p>
	</div>
</div>
