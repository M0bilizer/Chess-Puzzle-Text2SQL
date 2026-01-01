<script lang="ts">
	import type { Move } from 'chess.js';
	import { onMount } from 'svelte';
	import { Chess } from 'svelte-chess';
	import type { Game } from '../types/puzzle';
	import { playSound } from '../utils/playSound';
	import { getFirstMoveColor } from '../utils/getFirstMoveColor';

	interface Props {
		puzzle: Game;
		onStart?: () => void;
		onCorrectMove?: (move: Move) => void;
		onWrongMove?: (move: Move) => void;
		onEnd?: () => void;
	}
	let { puzzle, onStart, onCorrectMove, onWrongMove, onEnd }: Props = $props();

	class PuzzleState {
		private chess: Chess;
		private puzzle: Game;
		private boardElement: HTMLElement;

		private _positionIndex: number = $state(-1);
		private isProgrammaticMove: boolean = $state(false);

		private onStart?: () => void;
		private onCorrectMove?: (move: Move) => void;
		private onWrongMove?: (move: Move) => void;
		private onEnd?: () => void;

		constructor(
			chess: Chess,
			puzzle: Game,
			boardElement: HTMLElement,
			callbacks: {
				onStart?: () => void;
				onCorrectMove?: (move: Move) => void;
				onWrongMove?: (move: Move) => void;
				onEnd?: () => void;
			}
		) {
			this.chess = chess;
			this.puzzle = puzzle;
			this.boardElement = boardElement;
			this.onStart = callbacks.onStart;
			this.onCorrectMove = callbacks.onCorrectMove;
			this.onWrongMove = callbacks.onWrongMove;
			this.onEnd = callbacks.onEnd;

			this.reset();
		}

		public isPlayerTurn = $derived.by(() => this._positionIndex % 2 === 0);
		public isComplete = $derived.by(() => this._positionIndex >= this.puzzle.moves.length * 2 - 1);
		public canGoBack = $derived.by(() => this._positionIndex > -1);
		public canGoForward = $derived.by(() => this._positionIndex < this.puzzle.moves.length * 2 - 1);

		private waitForAnimations(): Promise<void> {
			return new Promise<void>((resolve) => {
				const checkAnimations = () => {
					const hasAnimation = this.boardElement.querySelector('.anim');
					if (!hasAnimation) {
						resolve();
					} else {
						requestAnimationFrame(checkAnimations);
					}
				};
				requestAnimationFrame(checkAnimations);
			});
		}

		private getExpectedMoveAtPosition(posIndex: number): string | null {
			if (posIndex < 0 || posIndex >= this.puzzle.moves.length * 2) {
				return null;
			}
			const pairIndex = Math.floor(posIndex / 2);
			const isComputerMove = posIndex % 2 === 0;
			const movePair = this.puzzle.moves[pairIndex];
			if (!movePair) return null;
			return isComputerMove ? movePair.computer : movePair.player;
		}

		reset(): void {
			this.isProgrammaticMove = true;
			this.chess.load(this.puzzle.fen);
			this._positionIndex = -1;
			this.isProgrammaticMove = false;
		}

		async start(): Promise<void> {
			this.onStart?.();
			this.reset();
			await new Promise((resolve) => setTimeout(resolve, 500));
			await this.forward();
		}

		async back(): Promise<void> {
			if (!this.canGoBack) return;

			this.isProgrammaticMove = true;
			this.chess.undo();
			this._positionIndex--;
			this.isProgrammaticMove = false;
		}

		async forward(): Promise<void> {
			if (!this.canGoForward) return;

			const nextMove = this.getExpectedMoveAtPosition(this._positionIndex + 1);
			if (!nextMove) return;

			this.isProgrammaticMove = true;
			this.chess.move(nextMove);
			this._positionIndex++;
			this.isProgrammaticMove = false;
		}

		async end(): Promise<void> {
			this.isProgrammaticMove = true;
			this.chess.load(this.puzzle.fen);

			for (let i = 0; i < this.puzzle.moves.length * 2; i++) {
				const move = this.getExpectedMoveAtPosition(i);
				if (move) {
					this.chess.move(move);
				}
			}

			this._positionIndex = this.puzzle.moves.length * 2 - 1;
			this.isProgrammaticMove = false;
		}

		async handlePlayerMove(move: Move): Promise<boolean> {
			if (!this.isPlayerTurn || this.isProgrammaticMove) {
				return false;
			}
			const expectedMove = this.getExpectedMoveAtPosition(this._positionIndex + 1);
			if (!expectedMove || move.lan !== expectedMove) {
				this.onWrongMove?.(move);
				await this.waitForAnimations();
				setTimeout(() => {
					this.chess.undo();
				}, 100);
				return false;
			}

			this.onCorrectMove?.(move);
			this._positionIndex++;
			if (this.isComplete) {
				this.onEnd?.();
				return true;
			}
			await this.waitForAnimations();
			setTimeout(async () => {
				await this.forward();
			}, 100);
			return true;
		}
	}

	let boardElement: HTMLElement;
	let chess: Chess;

	let puzzleState: PuzzleState;

	onMount(() => {
		puzzleState = new PuzzleState(chess, puzzle, boardElement, {
			onStart,
			onCorrectMove,
			onWrongMove,
			onEnd
		});
	});

	export async function back() {
		await puzzleState.back();
	}

	export async function reset() {
		puzzleState.reset();
	}

	export async function forward() {
		await puzzleState.forward();
	}

	export async function end() {
		await puzzleState.end();
	}

	async function moveListener(event: CustomEvent<Move>) {
		const { detail } = event;
		playSound(!!detail.captured);
		await puzzleState.handlePlayerMove(detail);
	}

	function start() {
		puzzleState.start();
	}
</script>

<div bind:this={boardElement}>
	<Chess
		bind:this={chess}
		on:ready={start}
		orientation={getFirstMoveColor(puzzle.fen)}
		on:move={moveListener}
	/>
</div>
