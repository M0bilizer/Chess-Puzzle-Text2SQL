<script lang="ts">
	import type { Move } from 'chess.js';
	import { onMount } from 'svelte';
	import { Chess } from 'svelte-chess';
	import type { Game } from '../types/puzzle';
	import { playSound } from '../utils/playSound';
	import { getPlayerColor } from '../utils/getPlayerColor';

	const DEFAULT_SETTINGS = {
		computerMoveDelay: 250,
		flipOrientation: false,
		muted: false
	};

	interface Props {
		puzzle: Game;
		settings?: Partial<typeof DEFAULT_SETTINGS>;
		onStart?: () => void;
		onCorrectMove?: (move: Move) => void;
		onWrongMove?: (move: Move) => void;
		onEnd?: () => void;
	}
	let {
		puzzle,
		settings: propSettings,
		onStart,
		onCorrectMove,
		onWrongMove,
		onEnd
	}: Props = $props();

	const settings = $derived({
		...DEFAULT_SETTINGS,
		...propSettings
	});

	class PuzzleState {
		private chess: Chess;
		private puzzle: Game;
		private boardElement: HTMLElement;

		private _positionIndex: number = $state(-1);
		private _jumpingIndex: number | null = $state(null);
		private isProgrammaticMove: boolean = $state(false);
		private shouldPlaySound: boolean = $state(true);

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
		}

		public isPlayerTurn = $derived.by(() => this._positionIndex % 2 === 0);
		public isComplete = $derived.by(() => this._positionIndex >= this.puzzle.moves.length * 2 - 1);
		public canGoBack = $derived.by(() => this._positionIndex > -1);
		public canGoForward = $derived.by(() => this._positionIndex < this.puzzle.moves.length * 2 - 1);
		public isInJump = $derived.by(() => this._jumpingIndex != null);

		private waitForAnimations(): Promise<void> {
			return new Promise<void>((resolve) => {
				const hasAnimation = this.boardElement.querySelector('.anim');
				if (!hasAnimation) {
					resolve();
					return;
				}
				const observer = new MutationObserver(() => {
					const currentAnimations = this.boardElement.querySelector('.anim');
					if (!currentAnimations) {
						observer.disconnect();
						resolve();
					}
				});
				observer.observe(this.boardElement, {
					childList: true,
					subtree: true,
					attributes: true,
					attributeFilter: ['class']
				});
			});
		}

		game = {
			start: async (): Promise<void> => {
				this.onStart?.();
				this.chess.load(this.puzzle.fen);
				this._positionIndex = -1;
				await new Promise((resolve) => setTimeout(resolve, 500));
				await this.game.playComputerMove();
			},

			handleWrongMove: async (move: Move): Promise<void> => {
				this.onWrongMove?.(move);
				await this.waitForAnimations();
				setTimeout(() => {
					this.chess.undo();
				}, 100);
			},

			handleCorrectMove: async (move: Move): Promise<void> => {
				this.onCorrectMove?.(move);
				this._positionIndex++;

				if (this.isComplete) {
					this.onEnd?.();
					return;
				}

				await this.waitForAnimations();
				setTimeout(async () => {
					await this.game.playComputerMove();
				});
			},

			getExpectedMoveAtPosition: (posIndex: number): string | null => {
				if (posIndex < 0 || posIndex >= this.puzzle.moves.length * 2) {
					return null;
				}
				const pairIndex = Math.floor(posIndex / 2);
				const isComputerMove = posIndex % 2 === 0;
				const movePair = this.puzzle.moves[pairIndex];
				if (!movePair) return null;
				return isComputerMove ? movePair.computer : movePair.player;
			},

			playComputerMove: async (): Promise<void> => {
				if (!this.canGoForward) return;

				const nextMove = this.game.getExpectedMoveAtPosition(this._positionIndex + 1);
				if (!nextMove) return;

				await new Promise((resolve) => setTimeout(resolve, settings.computerMoveDelay));
				this.isProgrammaticMove = true;
				this.chess.move(nextMove);
				await this.waitForAnimations();
				this._positionIndex++;
				this.isProgrammaticMove = false;
			}
		};

		jump = {
			config: {
				init: () => {
					this._jumpingIndex = this._positionIndex;
				},

				teardown: () => {
					this._jumpingIndex = null;
				}
			},

			first: () => {
				if (!this.isInJump) this.jump.config.init();
				this.isProgrammaticMove = true;
				this.chess.load(this.puzzle.fen);
				this._jumpingIndex = -1;
				this.isProgrammaticMove = false;
			},

			back: () => {
				if (!this.isInJump) this.jump.config.init();
				if (this._jumpingIndex == -1) return;
				this.isProgrammaticMove = true;
				this.chess.undo();
				this._jumpingIndex!--;
				this.isProgrammaticMove = false;
			},

			forward: () => {
				if (!this.isInJump) return;
				this.isProgrammaticMove = true;
				const move = this.game.getExpectedMoveAtPosition(this._jumpingIndex! + 1)!;
				this.chess.move(move);
				this._jumpingIndex!++;
				this.isProgrammaticMove = false;
				if (this._jumpingIndex == this._positionIndex) this.jump.config.teardown();
			},
			last: () => {
				if (!this.isInJump) return;
				this.isProgrammaticMove = true;
				this.shouldPlaySound = false;
				this.chess.load(this.puzzle.fen);
				for (let i = -1; i < this._positionIndex - 1; i++) {
					const move = this.game.getExpectedMoveAtPosition(i + 1)!;
					this.chess.move(move);
				}
				this.shouldPlaySound = true;
				const move = this.game.getExpectedMoveAtPosition(this._positionIndex);
				this.chess.move(move!);
				this.isProgrammaticMove = false;
				this.jump.config.teardown();
			}
		};

		async handleMove(move: Move): Promise<boolean> {
			if (this.shouldPlaySound && !settings.muted) {
				playSound(!!move.captured);
			}
			if (!this.isPlayerTurn || this.isProgrammaticMove) {
				return false;
			}
			const expectedMove = this.game.getExpectedMoveAtPosition(this._positionIndex + 1);
			if (!expectedMove || move.lan !== expectedMove) {
				await this.game.handleWrongMove(move);
				return false;
			} else {
				await this.game.handleCorrectMove(move);
				return true;
			}
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
		puzzleState.jump.back();
	}

	export async function reset() {
		puzzleState.jump.first();
	}

	export async function forward() {
		puzzleState.jump.forward();
	}

	export async function end() {
		puzzleState.jump.last();
	}

	async function moveListener(event: CustomEvent<Move>) {
		const { detail } = event;
		await puzzleState.handleMove(detail);
	}

	function start() {
		puzzleState.game.start();
	}
</script>

<div bind:this={boardElement}>
	<Chess
		bind:this={chess}
		on:ready={start}
		orientation={getPlayerColor(puzzle.fen, settings.flipOrientation)}
		on:move={moveListener}
	/>
</div>
