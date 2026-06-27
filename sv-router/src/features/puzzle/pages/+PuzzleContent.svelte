<script lang="ts">
	import MainWithAsidePage from '@/common/components/MainWithAsidePage.svelte';
	import ChessDescription from '../components/ChessDescription.svelte';
	import { playSound } from '../utils';
	import MoveTable from '../components/MoveTable.svelte';
	import JumpRow from '../components/JumpRow.svelte';
	import Chessboard from '../components/Chessboard.svelte';
	import { PuzzleSession, type Puzzle } from '../type.svelte';
	import type { Move } from 'chess.js';
	import { onDestroy, onMount } from 'svelte';
	import { preferencesState } from '@/features/settings/preferences-state';
	import { StateHistory } from 'runed';
	import Playlist from '../components/Playlist.svelte';
	import MoveFeedback from '../components/MoveFeedback.svelte';

	type Props = {
		puzzle: Puzzle;
		hasNext: boolean;
	};
	let { puzzle, hasNext }: Props = $props();

	let session = $derived(new PuzzleSession(puzzle));
	let chessboard = $state<Chessboard | null>(null);
	let currentIndex = $state(0);
	let latestIndex = $state(0);
	let isComplete = $derived(latestIndex >= session.getTotalMoves());
	let settings = preferencesState.current;

	let movePlayed = $state<{
		index: number;
		move: Move;
		isComputer: boolean;
		isCorrect: boolean;
	}>();
	let movesPlayed = new StateHistory(
		() => movePlayed,
		(mP) => (movePlayed = mP)
	);

	let fen = $derived(session.getFenAt(0));
	let orientation = $derived.by(() => {
		const playerColor = session.getPlayerColor() === 'w' ? 'white' : 'black';
		const flip = preferencesState.current.flipOrientation;
		return flip ? (playerColor === 'white' ? 'black' : 'white') : playerColor;
	}) as 'white' | 'black';

	async function onMove(move: Move) {
		if (!chessboard) return;
		const isCorrect = session.makeMove(currentIndex, move);

		if (!isCorrect) {
			movePlayed = {
				index: currentIndex,
				move: move,
				isComputer: false,
				isCorrect: false
			};
			currentIndex++;
			// don't update latestIndex so that isCompleted don't get flipped to true if this is last move
			if (settings.waitForAnimation) {
				await chessboard.waitForAnimations();
				await new Promise((resolve) => setTimeout(resolve, 33));
			}
			chessboard.undo();
			currentIndex--;
			if (settings.waitForAnimation) {
				await chessboard.waitForAnimations();
			}
			return;
		}
		movePlayed = {
			index: currentIndex,
			move: move,
			isComputer: false,
			isCorrect: true
		};
		currentIndex++;
		latestIndex++;
		if (settings.waitForAnimation) {
			await chessboard.waitForAnimations();
		}
		// Make the computer move
		if (!isComplete) {
			await new Promise((resolve) => setTimeout(resolve, settings.computerMoveDelay));
			const computerMove = session.getCorrectMoveAt(currentIndex);
			movePlayed = {
				index: currentIndex,
				move: computerMove,
				isComputer: true,
				isCorrect: true
			};
			currentIndex++;
			latestIndex++;
			chessboard.makeMove(computerMove.from, computerMove.to);
			if (settings.waitForAnimation) {
				await chessboard.waitForAnimations();
			}
			return;
		}
	}

	onMount(async () => {
		if (settings.waitForAnimation) {
			await chessboard?.waitForAnimations();
			await new Promise((resolve) => setTimeout(resolve, 33));
		}
		// make the first computer move
		movePlayed = {
			index: currentIndex,
			move: session.getCorrectMoveAt(currentIndex),
			isComputer: true,
			isCorrect: true
		};
		currentIndex++;
		latestIndex++;

		// don't use programmtic move since sound might crash
		fen = session.getFenAt(currentIndex)!;
	});

	const onHint = () => {
		const move = session.getCorrectMoveAt(currentIndex);
		chessboard?.selectSquare(move.from);
	};

	const onSolution = async () => {
		const move = session.getCorrectMoveAt(currentIndex);
		chessboard?.makeMove(move.from, move.to);
		if (settings.waitForAnimation) await chessboard?.waitForAnimations();
		onMove(move);
	};

	const onJumpToIndex = (index: number) => {
		currentIndex = index;
		fen = session.getFenAt(index)!;
	};

	const canGoBack = $derived(currentIndex > 0);
	const canGoForward = $derived(currentIndex < latestIndex);
	const onReset = () => {
		currentIndex = 0;
		fen = session.getFenAt(0)!;
	};
	const onBack = () => {
		if (canGoBack) {
			currentIndex--;
			fen = session.getFenAt(currentIndex)!;
		}
	};
	const onForward = () => {
		if (canGoForward) {
			const move = session.getCorrectMoveAt(currentIndex);
			if (!settings.muted) playSound(move.captured !== undefined);
			currentIndex++;
			fen = session.getFenAt(currentIndex)!;
		}
	};
	const onEnd = () => {
		currentIndex = latestIndex;
		fen = session.getFenAt(currentIndex);
	};

	let playerMoveResult = $derived.by(() => {
		const lastPlayerMove = movesPlayed.log
			.map((it) => it.snapshot)
			.findLast((it) => !it?.isComputer);
		if (!lastPlayerMove) return undefined;
		return (lastPlayerMove.isCorrect ? 'correct' : 'wrong') as 'correct' | 'wrong';
	});

	/** This sets the aside to always be the same height as the chessboard */
	let height: number | undefined = $state(0);
	let resizeObserver: ResizeObserver;
	onMount(() => {
		function updateHeight() {
			height = chessboard?.getElement()?.clientHeight;
		}
		updateHeight();
		resizeObserver = new ResizeObserver(() => {
			updateHeight();
		});
		if (chessboard?.getElement() !== null) {
			resizeObserver.observe(chessboard?.getElement() as Element);
		}
	});
	onDestroy(() => {
		resizeObserver?.disconnect();
	});
</script>

<MainWithAsidePage>
	<aside style:height="{height}px">
		<Playlist />
	</aside>
	<main class="space-y-0 lg:space-y-4">
		<Chessboard
			bind:this={chessboard}
			bind:fen
			{onMove}
			{orientation}
			bind:settings={preferencesState.current}
		/>
		<ChessDescription {puzzle} class="hidden md:block" />
	</main>
	<aside>
		<MoveTable
			bind:currentIndex
			bind:latestIndex
			movesPlayed={movesPlayed.log.map((it) => it.snapshot)}
			playerColor={session.getPlayerColor()}
			{onJumpToIndex}
			class="hidden md:block"
		/>
		<MoveFeedback
			playerColor={session.getPlayerColor()}
			{isComplete}
			moveResult={playerMoveResult}
			{onHint}
			{onSolution}
			{hasNext}
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
	</aside>
</MainWithAsidePage>
