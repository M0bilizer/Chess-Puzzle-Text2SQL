<script lang="ts">
	import Fa6SolidSquareCaretRightRight from 'virtual:icons/fa6-solid/square-caret-right';
	import Fa6SolidChessKing from 'virtual:icons/fa6-solid/chess-king';
	import { modalState } from '$lib/stores/congratulationModalStore';
	import CongratulationModal from '$lib/components/modals/CongratulationModal.svelte';
	import { loadNextGame, saveGame } from '$lib/utils/storeUtils';
	import { currentGame, isAllFinished } from '$lib/stores/currentGameStore';
	import { decrementJump, incrementJump } from '$lib/stores/jumpStore';

	let hasWon = false;
	let orientation: 'w' | 'b' = 'w';
	currentGame.subscribe((state) => {
		hasWon = state.game.hasWon;
		orientation = state.game.orientation;
	});

	function handleClick() {
		saveGame();
		if (isAllFinished()) {
			modalState.set({ open: true });
		} else {
			loadNextGame();
		}
	}

	function handleUndo() {
		decrementJump();
	}

	function handleRedo() {
		incrementJump();
	}
</script>

<div
	class="card grid h-max w-full max-w-md grid-rows-[auto_1fr_auto] border-[1px] shadow-xl border-surface-200-800 preset-filled-surface-100-900"
>
	<div class="h-20"></div>
	{#if hasWon}
		<button
			class="flex flex-row items-center gap-2 py-10 pl-2 preset-filled-primary-100-900 hover:preset-filled-primary-200-800"
			on:click={() => handleClick()}
		>
			<Fa6SolidSquareCaretRightRight class="size-16 text-tertiary-500" />
			<span class="h3">Next Puzzle</span>
		</button>
	{:else}
		<div
			class="flex flex-row items-center justify-center gap-2 py-8 align-middle"
			class:white={orientation === 'w'}
			class:black={orientation === 'b'}
		>
			<Fa6SolidChessKing class="size-16 text-inherit"></Fa6SolidChessKing>
			<h3 class="h3 font-semibold text-inherit">Your Turn</h3>
		</div>
	{/if}
	<div class="h-20">
		<button class="btn" on:click={() => handleUndo()}>Undo</button>
		<button class="btn" on:click={() => handleRedo()}>Redo</button>
	</div>
</div>
<CongratulationModal />

<style>
	.white {
		@apply text-primary-50;
	}

	.black {
		@apply text-surface-950;
	}
</style>
