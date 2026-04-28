<script lang="ts">
	import Game from '@/features/puzzle/components/Game.svelte';
	import MainWithAsidePage from '@/common/components/MainWithAsidePage.svelte';
	import ChessDescription from '../components/ChessDescription.svelte';
	import { puzzle as puzzleStub } from '../api/puzzle-stub';
	import { puzzleToGame } from '../utils';
	import type { Engine } from '../type.svelte';
	import { getPlayerColor } from '../components/get-player-color';
	import MoveFeedback from '../components/MoveFeedback.svelte';

	const puzzle = puzzleStub[3];
	const game = puzzleToGame(puzzle);

	// Engine will be init by Game
	let engine: Engine | undefined = $state();
	let moveResult = $state<'correct' | 'wrong' | null>(null);

	function onCorrectMove() {
		moveResult = 'correct';
	}
	function onWrongMove() {
		moveResult = 'wrong';
	}

	// Reactive state from engine (ready for future use)
	let gameState = $derived(engine?.getState());
	let isPlayerTurn = $derived(gameState?.isPlayerTurn);
	let isComplete = $derived(gameState?.isComplete);
	let canGoBack = $derived(gameState?.canGoBack);
	let canGoForward = $derived(gameState?.canGoForward);
	let positionIndex = $derived(gameState?.positionIndex);

	function reset() {
		engine?.jump.first();
	}

	function back() {
		engine?.jump.back();
	}

	function forward() {
		engine?.jump.forward();
	}

	function end() {
		engine?.jump.last();
	}

	const settings: Record<string, unknown> = {} as Record<string, unknown>;

	const playerColor = getPlayerColor(game.fen, (settings?.flipOrientation as boolean) || false);
</script>

<MainWithAsidePage>
	<main class="space-y-4">
		<Game {game} bind:engine {onCorrectMove} {onWrongMove} />
		<ChessDescription {puzzle} />
	</main>
	<aside>
		<MoveFeedback {playerColor} isComplete={isComplete ?? false} {moveResult} />
	</aside>
</MainWithAsidePage>
