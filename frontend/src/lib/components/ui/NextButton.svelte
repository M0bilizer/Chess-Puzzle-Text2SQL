<script lang="ts">
	import Fa6SolidSquareCaretRightRight from 'virtual:icons/fa6-solid/square-caret-right';
	import Fa6SolidChessKing from 'virtual:icons/fa6-solid/chess-king';
	import { loadNextGame, saveGame } from '$lib/utils/storeUtils';
	import { currentGame, isAllFinished } from '$lib/stores/currentGameStore';
	import { congratulationModalState } from '$lib/stores/modalStore';

	let hasWon = false;
	let orientation: 'w' | 'b' = 'w';
	currentGame.subscribe((state) => {
		hasWon = state.game.hasWon;
		orientation = state.game.orientation;
	});

	function handleClick() {
		saveGame();
		if (isAllFinished()) {
			congratulationModalState.set({ open: true });
		} else {
			loadNextGame();
		}
	}
</script>

<div
	class="card h-[250px] w-full max-w-md border-[1px] py-12 align-middle shadow-xl border-surface-200-800 preset-filled-surface-100-900"
>
	{#if hasWon}
		<button
			class="flex w-full flex-row items-center gap-2 py-10 pl-2 preset-filled-primary-100-900 hover:preset-filled-primary-200-800"
			on:click={() => handleClick()}
		>
			<Fa6SolidSquareCaretRightRight class="size-16 text-tertiary-500" />
			<span class="h3">Next Puzzle</span>
		</button>
	{:else}
		<div
			class="flex flex-col items-center gap-2"
			class:white={orientation === 'w'}
			class:black={orientation === 'b'}
		>
			<Fa6SolidChessKing class="size-16 text-inherit"></Fa6SolidChessKing>
			<div class="flex flex-col items-center">
				<h3 class="h3 font-semibold text-inherit">Your Turn</h3>
				<span
					class="text-sm font-semibold text-surface-500"
					class:white={orientation === 'w'}
					class:black={orientation === 'b'}
				>
					Find the best move for {orientation === 'w' ? 'white' : 'black'}
				</span>
			</div>
		</div>
	{/if}
</div>

<style>
	.white {
		@apply text-primary-50;
	}

	.black {
		@apply text-surface-950;
	}
</style>
