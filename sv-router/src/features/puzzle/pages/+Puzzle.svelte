<script lang="ts">
	import Game from '@/features/puzzle/components/Game.svelte';
	import MainWithAsidePage from '@/common/components/MainWithAsidePage.svelte';
	import ChessDescription from '../components/ChessDescription.svelte';
	import { puzzle as puzzleStub } from '../api/puzzle-stub';
	import { puzzleToGame } from '../utils';
	import type { Engine } from '../type.svelte';
	import { getPlayerColor } from '../components/get-player-color';
	import MoveFeedback from '../components/MoveFeedback.svelte';
	import MoveTable from '../components/MoveTable.svelte';
	import JumpRow from '../components/JumpRow.svelte';

	const puzzle = puzzleStub[3];
	const game = puzzleToGame(puzzle);

	// Engine will be init by Game
	let engine: Engine | undefined = $state();
	let moveResult = $state<'correct' | 'wrong' | null>(null);
	let wrongAttempts = $state<Map<number, string>>(new Map());

	function onCorrectMove() {
		moveResult = 'correct';
	}
	function onWrongMove() {
		moveResult = 'wrong';
	}

	function onMoveMade(move: {
		move: string;
		isComputer: boolean;
		isCorrect?: boolean;
		positionIndex: number;
	}) {
		if (!move.isCorrect) {
			wrongAttempts.set(move.positionIndex, move.move);
		}
	}

	// Reactive state from engine (ready for future use)
	let gameState = $derived(engine?.getState());
	let isPlayerTurn = $derived(gameState?.isPlayerTurn);
	let isComplete = $derived(gameState?.isComplete);
	let canGoBack = $derived(gameState?.canGoBackInJump);
	let canGoForward = $derived(gameState?.canGoForwardInJump);
	let latestIndex = $derived(gameState?.latestIndex);

	function onReset() {
		engine?.jump.first();
	}

	function onBack() {
		engine?.jump.back();
	}

	function onForward() {
		engine?.jump.forward();
	}

	function onEnd() {
		engine?.jump.last();
	}

	function onJumpToIndex(index: number) {
		engine?.jump.to(index);
	}

	const settings: Record<string, unknown> = {} as Record<string, unknown>;

	const playerColor = getPlayerColor(game.fen, (settings?.flipOrientation as boolean) || false);
</script>

<MainWithAsidePage>
	<main class="space-y-4">
		<Game {game} bind:engine {onCorrectMove} {onWrongMove} {onMoveMade} />
		<ChessDescription {puzzle} />
	</main>
	<aside>
		{#if gameState}
			<MoveTable {gameState} {onJumpToIndex} />
		{/if}
		<MoveFeedback {playerColor} isComplete={isComplete ?? false} {moveResult} />
		<JumpRow
			{onReset}
			{onBack}
			{onForward}
			{onEnd}
			canGoBack={canGoBack || false}
			canGoForward={canGoForward || false}
		/>
	</aside>
</MainWithAsidePage>
