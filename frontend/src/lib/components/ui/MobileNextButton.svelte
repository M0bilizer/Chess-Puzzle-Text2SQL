<script lang="ts">
	import Fa6SolidSquareCaretRightRight from 'virtual:icons/fa6-solid/square-caret-right';
	import Fa6SolidChessKing from 'virtual:icons/fa6-solid/chess-king';
	import Fa6SolidXMark from 'virtual:icons/fa6-solid/xmark';
	import Fa6SolidCheck from 'virtual:icons/fa6-solid/check';
	import { loadNextGame, saveGame } from '$lib/utils/storeUtils';
	import { isAllFinished } from '$lib/stores/currentGameStore';
	import { congratulationModalState } from '$lib/stores/modalStore';
	import { feedbackState, feedbackStore } from '$lib/stores/feedbackStore';

	let currentState = $state(feedbackState.white);

	feedbackStore.subscribe((state) => {
		currentState = state;
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
	class="card h-[105px] border-[1px] align-middle shadow-xl border-surface-200-800 preset-filled-surface-100-900"
>
	{#if currentState === feedbackState.won}
		<button
			class="flex h-full w-full flex-row items-center gap-2 pl-2 preset-filled-primary-100-900 hover:preset-filled-primary-200-800"
			onclick={() => handleClick()}
		>
			<Fa6SolidSquareCaretRightRight class="size-16 text-tertiary-500" />
			<span class="h3">Next Puzzle</span>
		</button>
	{:else if currentState === feedbackState.correct}
		<div class="flex h-full flex-row items-center justify-center gap-2 align-middle">
			<Fa6SolidCheck class="size-16 text-primary-500" />
			<div class="flex flex-col">
				<h3 class="h5 font-bold text-tertiary-50">Best move!</h3>
				<span class="text-tertiary-400">Keep going...</span>
			</div>
		</div>
	{:else if currentState === feedbackState.wrong}
		<div class="flex h-full flex-row items-center justify-center gap-2 align-middle">
			<Fa6SolidXMark class="size-16 text-error-500" />
			<div class="flex flex-col">
				<h3 class="h5 font-bold text-tertiary-50">That's not the move!</h3>
				<span class="text-tertiary-400">Try something else.</span>
			</div>
		</div>
	{:else}
		<div
			class="flex h-full w-full flex-row items-center gap-2 p-2 align-middle"
			class:white={currentState === feedbackState.white}
			class:black={currentState === feedbackState.black}
		>
			<Fa6SolidChessKing class="size-16 text-inherit"></Fa6SolidChessKing>
			<div class="flex flex-col items-center">
				<h3 class="h3 font-semibold text-inherit">Your Turn</h3>
				<span
					class="text-sm font-semibold text-surface-500"
					class:white={currentState === feedbackState.white}
					class:black={currentState === feedbackState.black}
				>
					Find the best move for {currentState === feedbackState.white ? 'white' : 'black'}
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
