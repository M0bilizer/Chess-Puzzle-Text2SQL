<script lang="ts">
	import { preferencesState } from '@/features/settings/preferences-state';
	import type { Move } from 'chess.js';
	import Chessboard from '../components/Chessboard.svelte';
	import ChessDescription from '../components/ChessDescription.svelte';
	import JumpRow from '../components/JumpRow.svelte';
	import MobileCurrentCollectionView from '../components/MobileCurrentCollectionView.svelte';
	import MoveFeedback from '../components/MoveFeedback.svelte';
	import MoveTable from '../components/MoveTable.svelte';
	import { PuzzleGame, type Puzzle } from '../type.svelte';
	import { playSound } from '../utils';

	type Props = {
		puzzle: Puzzle;
		game: PuzzleGame;
		hasNext: boolean;
		onComplete: () => void;
		onNext: () => void;
	};
	let { puzzle, game, hasNext, onComplete, onNext }: Props = $props();

	let chessboard = $state<Chessboard | null>(null);
	let isComplete = $derived(game.latestIndex >= game.getTotalMoves());
	let settings = preferencesState.current;

	let movesPlayed = $derived(game.movesPlayed);

	let fen = $derived(game.getFenAt(0));
	let orientation = $derived.by(() => {
		const playerColor = game.getPlayerColor() === 'w' ? 'white' : 'black';
		const flip = preferencesState.current.flipOrientation;
		return flip ? (playerColor === 'white' ? 'black' : 'white') : playerColor;
	}) as 'white' | 'black';

	async function onMove(move: Move) {
		if (!chessboard) return;
		const isCorrect = game.makeMove(game.currentIndex, move);

		if (!isCorrect) {
			// don't update latestIndex so that isCompleted don't get flipped to true if this is last move
			if (settings.waitForAnimation) {
				await chessboard.waitForAnimations();
				await new Promise((resolve) => setTimeout(resolve, 33));
			}
			chessboard.undo();
			game.currentIndex--;
			if (settings.waitForAnimation) {
				await chessboard.waitForAnimations();
			}
			return;
		}
		if (settings.waitForAnimation) {
			await chessboard.waitForAnimations();
		}
		// Make the computer move
		if (!isComplete) {
			await new Promise((resolve) => setTimeout(resolve, settings.computerMoveDelay));
			const computerMove = game.getCorrectMoveAt(game.currentIndex);
			game.makeMove(game.currentIndex, computerMove);
			chessboard.makeMove(computerMove.from, computerMove.to);
			if (settings.waitForAnimation) {
				await chessboard.waitForAnimations();
			}
			return;
		} else {
			onComplete();
		}
	}

	export async function startGame() {
		if (settings.waitForAnimation) {
			await chessboard?.waitForAnimations();
			await new Promise((resolve) => setTimeout(resolve, 33));
		}
		// make the first computer move
		const computerMove = game.getCorrectMoveAt(game.currentIndex);
		game.makeMove(game.currentIndex, computerMove);

		// don't use programmtic move since sound might crash
		fen = game.getFenAt(game.currentIndex)!;
	}

	const onHint = () => {
		const move = game.getCorrectMoveAt(game.currentIndex);
		chessboard?.selectSquare(move.from);
	};

	const onSolution = async () => {
		const move = game.getCorrectMoveAt(game.currentIndex);
		chessboard?.makeMove(move.from, move.to);
		if (settings.waitForAnimation) await chessboard?.waitForAnimations();
		onMove(move);
	};

	const onJumpToIndex = (index: number) => {
		game.currentIndex = index;
		fen = game.getFenAt(index)!;
	};

	const interactive = $derived(game.currentIndex === game.latestIndex);
	const canGoBack = $derived(game.currentIndex > 0);
	const canGoForward = $derived(game.currentIndex < game.latestIndex);
	const onReset = () => {
		game.currentIndex = 0;
		fen = game.getFenAt(0)!;
	};
	const onBack = () => {
		if (canGoBack) {
			game.currentIndex--;
			fen = game.getFenAt(game.currentIndex)!;
		}
	};
	const onForward = () => {
		if (canGoForward) {
			const move = game.getCorrectMoveAt(game.currentIndex);
			if (!settings.muted) playSound(move.captured !== undefined);
			game.currentIndex++;
			fen = game.getFenAt(game.currentIndex)!;
		}
	};
	const onEnd = () => {
		game.currentIndex = game.latestIndex;
		fen = game.getFenAt(game.currentIndex);
	};

	let playerMoveResult = $derived.by(() => {
		const lastPlayerMove = movesPlayed.log
			.map((it) => it.snapshot)
			.findLast((it) => !it?.isComputer);
		if (!lastPlayerMove) return undefined;
		return (lastPlayerMove.isCorrect ? 'correct' : 'wrong') as 'correct' | 'wrong';
	});
</script>

<section class="space-y-0 lg:space-y-4">
	<Chessboard
		bind:this={chessboard}
		bind:fen
		{onMove}
		{orientation}
		bind:settings={preferencesState.current}
		{interactive}
	/>
	<ChessDescription {puzzle} class="hidden md:block" />
</section>
<aside>
	<MoveTable
		bind:currentIndex={game.currentIndex}
		bind:latestIndex={game.latestIndex}
		movesPlayed={movesPlayed.log.map((it) => it.snapshot)}
		playerColor={game.getPlayerColor()}
		{onJumpToIndex}
		class="hidden md:block"
	/>
	<MoveFeedback
		playerColor={game.getPlayerColor()}
		{isComplete}
		moveResult={playerMoveResult}
		{onHint}
		{onSolution}
		{hasNext}
		{onNext}
	/>
	<JumpRow
		{onReset}
		{onBack}
		{onForward}
		{onEnd}
		canGoBack={canGoBack || false}
		canGoForward={canGoForward || false}
		bind:preferences={preferencesState.current}
	/>
	<MobileCurrentCollectionView class="block md:hidden" />
</aside>
