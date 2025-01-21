<script lang="ts">
	import Fa6SolidSquareCaretRightRight from 'virtual:icons/fa6-solid/square-caret-right';
	import Fa6SolidChessKing from 'virtual:icons/fa6-solid/chess-king';
	import { gameState, loadChess, puzzleList } from '$lib/stores/puzzleStore';

	let hasWon = false;
	let orientation: 'w' | 'b' = 'w';
	gameState.subscribe((state) => {
		hasWon = state.hasWon;
		orientation = state.orientation;
	});

	function loadNextGame() {
		if ($puzzleList.puzzles.length - 1 === $puzzleList.currentPuzzle) {
			console.log('finished');
			return;
		}
		puzzleList.update((currentState) => ({
			...currentState,
			currentPuzzle: currentState.currentPuzzle + 1
		}));
		const nextGame = $puzzleList.puzzles[$puzzleList.currentPuzzle];
		console.log(nextGame);
		loadChess(nextGame);
	}
</script>

<div
	class="card grid h-max w-full max-w-md grid-rows-[auto_1fr_auto] border-[1px] shadow-xl border-surface-200-800 preset-filled-surface-100-900"
>
	<div class="h-20"></div>
	{#if hasWon}
		<button
			class="flex flex-row items-center gap-2 py-10 pl-2 preset-filled-primary-100-900 hover:preset-filled-primary-200-800"
			on:click={() => loadNextGame()}
		>
			<Fa6SolidSquareCaretRightRight class="size-16 text-tertiary-500" />
			<h3 class="h3">Next Puzzle</h3>
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
	<div class="h-20"></div>
</div>

<style>
	.white {
		@apply text-primary-50;
	}

	.black {
		@apply text-surface-950;
	}
</style>
