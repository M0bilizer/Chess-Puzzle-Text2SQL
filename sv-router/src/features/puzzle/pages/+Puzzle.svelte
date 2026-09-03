<script lang="ts">
	import SimplePage from '@/common/components/SimplePage.svelte';
	import { preferencesState } from '@/features/settings/preferences-state';
	import { navigate, route } from '@/router';
	import type { Move } from 'chess.js';
	import { resource, watch } from 'runed';
	import { Chessground } from 'svelte5-chessground';
	import { Result } from 'typescript-result';

	import { getPuzzle } from '../api/puzzle.api';
	import ChessDescription from '../components/ChessDescription.svelte';
	import Chessboard from '../components/Chessboard.svelte';
	import CurrentCollectionView from '../components/CurrentCollectionView.svelte';
	import JumpRow from '../components/JumpRow.svelte';
	import MobileChessDescription from '../components/MobileChessDescription.svelte';
	import MobileCurrentCollectionView from '../components/MobileCurrentCollectionView.svelte';
	import MoveFeedback from '../components/MoveFeedback.svelte';
	import MoveTable from '../components/MoveTable.svelte';
	import { currentCollection } from '../store/current-collection.svelte';
	import { PuzzleGame } from '../type.svelte';
	import { playSound } from '../utils';

	let currentCollectionViewEl: CurrentCollectionView | undefined = $state();
	let mobileCollectionViewEl: MobileCurrentCollectionView | undefined = $state();
	let openDescription = $state(false);

	let chessboard = $state<Chessboard | null>(null);

	let { id } = $derived(route.getParams('/puzzle/:id'));
	const puzzleResource = resource(
		() => id,
		async (id) => {
			const result = await getPuzzle(id);
			return result.getOrThrow();
		}
	);

	let puzzle = $derived(puzzleResource.current);
	let game = $derived(puzzleResource.current ? new PuzzleGame(puzzleResource.current) : null);

	watch(
		() => id,
		(prevId, newId) => {
			if (prevId !== newId) {
				mobileCollectionViewEl?.scrollToCurrent();
				currentCollectionViewEl?.scrollToCurrent();
			}
		}
	);

	$effect(() => {
		if (game) {
			new Promise((resolve) => setTimeout(resolve, 100)).then(() => {
				if (game) startGame();
			});
		}
	});

	let isComplete = $derived(game ? game.latestIndex >= game.getTotalMoves() : false);
	let settings = preferencesState.current;
	let movesPlayed = $derived(game?.movesPlayed ?? { log: [] });

	let fen = $derived(game?.getFenAt(0) ?? '');
	let orientation = $derived.by(() => {
		if (!game) return 'white' as const;
		const playerColor = game.getPlayerColor() === 'w' ? 'white' : 'black';
		const flip = preferencesState.current.flipOrientation;
		return flip ? (playerColor === 'white' ? 'black' : 'white') : playerColor;
	}) as 'white' | 'black';

	async function onMove(move: Move) {
		if (!chessboard || !game) return;
		const isCorrect = game.makeMove(game.currentIndex, move);

		if (!isCorrect) {
			if (settings.waitForAnimation) {
				await chessboard.waitForAnimations();
				await new Promise((resolve) => setTimeout(resolve, 33));
			}
			const prevMove = Result.wrap((idx) => game.getCorrectMoveAt(idx))(
				game.currentIndex - 2
			).getOrElse(() => undefined);
			chessboard.setBoard({
				fen: game.fenAt(game.currentIndex - 1),
				lastMove: prevMove ? [prevMove.from, prevMove.to] : undefined,
				sound: { play: false }
			});
			game.currentIndex--;
			if (settings.waitForAnimation) {
				await chessboard.waitForAnimations();
			}
			return;
		}
		if (settings.waitForAnimation) {
			await chessboard.waitForAnimations();
		}
		if (!isComplete) {
			await new Promise((resolve) => setTimeout(resolve, settings.computerMoveDelay));
			const computerMove = game.getCorrectMoveAt(game.currentIndex);
			game.makeMove(game.currentIndex, computerMove, true);
			chessboard.makeMove(computerMove.from, computerMove.to);
			if (settings.waitForAnimation) {
				await chessboard.waitForAnimations();
			}
			return;
		} else {
			onComplete();
		}
	}

	async function startGame() {
		if (!game || !chessboard) return;
		if (settings.waitForAnimation) {
			await chessboard?.waitForAnimations();
			await new Promise((resolve) => setTimeout(resolve, 33));
		}
		const computerMove = game.getCorrectMoveAt(game.currentIndex);
		game.makeMove(game.currentIndex, computerMove, true);

		chessboard?.setBoard({
			fen: game.getFenAt(game.currentIndex),
			lastMove: [computerMove.from, computerMove.to],
			sound: { play: true, capture: computerMove.captured !== undefined }
		});
	}

	const onHint = () => {
		if (!game || !chessboard) return;
		const move = game.getCorrectMoveAt(game.currentIndex);
		chessboard?.selectSquare(move.from);
	};

	const onSolution = async () => {
		if (!game || !chessboard) return;
		const move = game.getCorrectMoveAt(game.currentIndex);
		chessboard?.makeMove(move.from, move.to);
		if (settings.waitForAnimation) await chessboard?.waitForAnimations();
		onMove(move);
	};

	const onJumpToIndex = (index: number) => {
		if (!game || !chessboard) return;
		game.currentIndex = index;
		const prevMove = Result.wrap((idx) => game.getCorrectMoveAt(idx))(index - 1).getOrElse(
			() => undefined
		);
		chessboard?.setBoard({
			fen: game.getFenAt(index),
			lastMove: prevMove ? [prevMove.from, prevMove.to] : undefined,
			sound: { play: false }
		});
	};

	const interactive = $derived(game?.currentIndex === game?.latestIndex);
	const canGoBack = $derived((game?.currentIndex ?? 0) > 0);
	const canGoForward = $derived((game?.currentIndex ?? 0) < (game?.latestIndex ?? 0));

	const onReset = () => {
		if (!game || !chessboard) return;
		game.currentIndex = 0;
		chessboard?.setBoard({
			fen: game.getFenAt(0),
			lastMove: undefined,
			sound: { play: false }
		});
	};

	const onBack = () => {
		if (!game || !chessboard || !canGoBack) return;
		game.currentIndex--;
		const prevMove = Result.wrap((idx) => game.getCorrectMoveAt(idx))(
			game.currentIndex - 1
		).getOrElse(() => undefined);
		chessboard?.setBoard({
			fen: game.getFenAt(game.currentIndex),
			lastMove: prevMove ? [prevMove.from, prevMove.to] : undefined,
			sound: { play: false }
		});
	};

	const onForward = () => {
		if (!game || !chessboard || !canGoForward) return;
		const move = game.getCorrectMoveAt(game.currentIndex);
		if (!settings.muted) playSound(move.captured !== undefined);
		game.currentIndex++;
		const prevMove = Result.wrap((idx) => game.getCorrectMoveAt(idx))(
			game.currentIndex - 1
		).getOrElse(() => undefined);
		chessboard?.setBoard({
			fen: game.getFenAt(game.currentIndex),
			lastMove: prevMove ? [prevMove.from, prevMove.to] : undefined,
			sound: { play: true, capture: move.captured !== undefined }
		});
	};

	const onEnd = () => {
		if (!game || !chessboard) return;
		game.currentIndex = game.latestIndex;
		const prevMove = Result.wrap((idx) => game.getCorrectMoveAt(idx))(
			game.currentIndex - 1
		).getOrElse(() => undefined);
		chessboard?.setBoard({
			fen: game.getFenAt(game.currentIndex),
			lastMove: prevMove ? [prevMove.from, prevMove.to] : undefined,
			sound: { play: false }
		});
	};

	let playerMoveResult = $derived.by(() => {
		const lastPlayerMove = movesPlayed.log
			.map((it) => it.snapshot)
			.findLast((it) => !it?.isComputer);
		if (!lastPlayerMove) return undefined;
		return (lastPlayerMove.isCorrect ? 'correct' : 'wrong') as 'correct' | 'wrong';
	});

	const onComplete = () => {
		currentCollection.setPuzzleResult(id, true);
	};

	const onNext = () => {
		if (currentCollection) {
			const result = currentCollection.getFirstUnsolved();
			if (!result) {
				console.error('No next puzzle');
				return;
			}
			navigate(`/puzzle/:id`, { params: { id: result.puzzleId } });
		}
	};

	const hasNext = $derived(currentCollection.next(id) !== undefined);
</script>

<!-- auto-rows-auto so grid cell's size is based on element. Important for the chessboard
     content-start so grid cell elements start at the top. -->
<SimplePage
	class=" md:space-y-0 grid grid-cols-1 md:grid-cols-[auto_1fr_330px]  space-y-2 auto-rows-auto content-start md:gap-x-8 md:gap-y-4 md:px-8 max-w-[1400px] mx-auto"
>
	<!-- First row  -->
	<aside class="space-y-1 shrink-0 max-h-[700px] hidden md:flex">
		<CurrentCollectionView
			bind:this={currentCollectionViewEl}
			currentId={id}
			class="hidden md:flex"
		/>
	</aside>

	<section class="aspect-square md:max-h-[700px]">
		{#if puzzleResource.error}
			<div class="rounded-lg border-2 border-dotted border-error-100-900 aspect-square"></div>
		{:else if puzzleResource.loading}
			<Chessground fen="8/8/8/8/8/8/8/8" class="animate-pulse" />
		{:else if puzzleResource.current && game}
			<Chessboard
				bind:this={chessboard}
				bind:fen
				{onMove}
				{orientation}
				bind:settings={preferencesState.current}
				{interactive}
			/>
		{/if}
	</section>

	<aside class="hidden md:block flex-col w-full max-h-[700px]">
		{#if puzzleResource.error}

		{:else if puzzleResource.loading}
			<div class="placeholder h-full"></div>
		{:else if puzzleResource.current && game !== null}
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
		{/if}
	</aside>
	<!-- Second row -->
	<aside class="hidden md:flex"></aside>
	<section class="hidden md:flex">
		{#if puzzleResource.error}

		{:else if puzzleResource.loading}
			<div class="flex-1 placeholder h-[77px]"></div>
		{:else if puzzleResource.current && game && puzzle !== undefined}
			<ChessDescription open={openDescription} {puzzle} class="hidden md:block" />
		{/if}
	</section>

	<aside class="flex flex-col gap-y-4 md:hidden w-full">
		{#if puzzleResource.current && game && puzzle !== undefined}
			<MobileChessDescription bind:open={openDescription} {puzzle} class="block md:hidden" />

			<MobileCurrentCollectionView
				bind:this={mobileCollectionViewEl}
				currentId={id}
				class="block md:hidden"
			/>
		{/if}
	</aside>
</SimplePage>
