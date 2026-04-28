<script lang="ts">
	type Props = {
		playerColor: 'w' | 'b';
		isComplete: boolean;
		moveResult?: 'correct' | 'wrong' | null;
	};
	let { playerColor, isComplete, moveResult = null }: Props = $props();

	const playerColorName = $derived(playerColor === 'w' ? 'white' : 'black');
</script>

<div
	class="flex h-[100px] w-full items-center justify-center rounded-lg preset-filled-surface-100-900 p-4"
>
	{#if isComplete}
		<div class="text-center">
			<span class="text-2xl">🎉 Success! 🎉</span>
			<div class="mt-1 text-sm">Puzzle completed!</div>
		</div>
	{:else if moveResult === 'wrong'}
		<div class="text-center">
			<span class="text-xl text-red-600">❌ That's not the move</span>
			<div class="mt-1 text-sm text-red-400">Try again!</div>
		</div>
	{:else if moveResult === 'correct'}
		<div class="text-center">
			<span class="text-xl text-green-600">✅ Best move! Keep going...</span>
			<div class="mt-1 text-sm text-green-400">Great job!</div>
		</div>
	{:else}
		<div class="text-center">
			<span class="text-xl">
				Find the best move for
				<span class="font-bold">
					{playerColor === 'w' ? '⚪' : '⚫'}
					{playerColorName}
				</span>
			</span>
			<div class="mt-1 text-sm">It's your turn!</div>
		</div>
	{/if}
</div>
