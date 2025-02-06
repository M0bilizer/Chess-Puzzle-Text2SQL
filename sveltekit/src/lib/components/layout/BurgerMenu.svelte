<script lang="ts">
	import { Popover, Switch } from '@skeletonlabs/skeleton-svelte';
	import Fa6SolidBars from 'virtual:icons/fa6-solid/bars';
	import { currentGame, skipCurrentGame } from '$lib/stores/currentGameStore';

	let puzzleId: string = $state('');
	let openState: boolean = $state(false);
	let flipped: boolean = $state(false);
	let disabled: boolean = $state(false);

	function handleSkipPuzzle() {
		skipCurrentGame();
	}

	currentGame.subscribe((state) => {
		puzzleId = state.list[state.index].puzzle.puzzleId;
		disabled = state.game.hasWon;
	});
</script>

<Popover
	bind:open={openState}
	positioning={{ placement: 'top-end' }}
	contentBase="card border-[1px] p-4 max-w-[320px] border-surface-200-800 preset-filled-surface-100-900 z-[1000]"
>
	{#snippet trigger()}
		<Fa6SolidBars class="size-4 text-tertiary-500" />
	{/snippet}
	{#snippet content()}
		<div class="flex flex-col gap-2">
			<span class="flex items-center justify-around align-middle">
				<span class="h6">Flip Board</span>
				<Switch name="example" bind:checked={flipped} />
			</span>
			<hr class="hr" />
			<button
				class="px-2 text-left text-sm hover:preset-tonal-primary"
				onclick={() => handleSkipPuzzle()}
				{disabled}
			>
				<span class="text-surface-500">Skip puzzle</span>
			</button>
			<a
				href="https://lichess.org/training/{puzzleId}"
				target="_blank"
				class="px-2 text-left text-sm text-surface-500 underline hover:preset-tonal-primary"
			>
				Open puzzle in Lichess
			</a>
		</div>
	{/snippet}
</Popover>
