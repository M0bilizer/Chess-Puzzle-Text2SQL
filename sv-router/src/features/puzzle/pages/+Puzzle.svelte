<script lang="ts">
	import MainWithAsidePage from '@/common/components/MainWithAsidePage.svelte';
	import ChessDescription from '../components/ChessDescription.svelte';
	import { puzzle as puzzleStub } from '../api/puzzle-stub';
	import { playSound } from '../utils';
	import MoveFeedback from '../components/MoveFeedback.svelte';
	import MoveTable from '../components/MoveTable.svelte';
	import JumpRow from '../components/JumpRow.svelte';
	import Chessboard from '../components/Chessboard.svelte';
	import { PuzzleSession } from '../type.svelte';
	import type { Move } from 'chess.js';
	import { onMount } from 'svelte';
	import { preferencesState } from '@/features/settings/preferences-state';
	import { StateHistory } from 'runed';

	const puzzle = puzzleStub[3];
	let session = new PuzzleSession(puzzle);
	let chessboard = $state<Chessboard | null>(null);
	let currentIndex = $state(0);
	let latestIndex = $state(0);
	let isComplete = $derived(latestIndex >= session.getTotalMoves());

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

	let fen = $state(session.getFenAt(0));
	let orientation = $derived(session.getPlayerColor() === 'w' ? 'white' : 'black') as
		| 'white'
		| 'black';

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
			latestIndex++;
			await chessboard.waitForAnimations();
			await chessboard.undo();
			currentIndex--;
			latestIndex--;
			await chessboard.waitForAnimations();
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
		await chessboard.waitForAnimations();
		// Make the computer move
		if (!isComplete) {
			const computerMove = session.getCorrectMoveAt(currentIndex);
			movePlayed = {
				index: currentIndex,
				move: computerMove,
				isComputer: true,
				isCorrect: true
			};
			await chessboard.makeMove(computerMove.from, computerMove.to);
			currentIndex++;
			latestIndex++;
			return;
		}
	}

	onMount(async () => {
		await chessboard?.waitForAnimations();
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
			playSound(move.captured !== undefined);
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
</script>

<MainWithAsidePage>
	<main class="space-y-0 lg:space-y-4">
		<Chessboard bind:this={chessboard} bind:fen {onMove} {orientation} />
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
