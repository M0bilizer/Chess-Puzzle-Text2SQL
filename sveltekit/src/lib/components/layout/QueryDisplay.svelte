<script lang="ts">
	import { currentGame } from '$lib/stores/currentGameStore';

	let query = '';
	let num = 0;

	currentGame.subscribe((state) => {
		query = state.query;
		num = state.list.length;
	});
</script>

<div
	class="card w-full min-w-0 max-w-md border-[1px] p-4 shadow-xl border-surface-200-800 preset-filled-surface-100-900"
>
	{#if num}
		<div class="flex flex-col">
			<span class="truncate">You searched <code class="code">{query}></code></span>
			<span>and got <code class="code">{num}</code> {num === 1 ? 'puzzle' : 'puzzles'}</span>
		</div>
	{:else}
		<h5 class="h5 text-center">No puzzles found...</h5>
	{/if}
</div>

<style>
</style>
