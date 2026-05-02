import type { Move } from 'chess.js';
import type { Chess } from 'svelte-chess';
import { playSound } from './components/play-sound';

export type Game = {
	fen: string;
	moves: { computer: string; player: string }[];
};

export type Puzzle = {
	id: number;
	puzzleId: string;
	fen: string;
	moves: string;
	rating: number;
	ratingDeviation: number;
	popularity: number;
	nbPlays: number;
	themes: string;
	gameUrl: string;
	openingTags: string;
};

export class GameState {
	private chess: Chess;
	private game: Game;

	private _positionIndex: number = $state(-1);
	private _jumpingIndex: number | null = $state(null);
	private _isProgrammaticMove: boolean = $state(false);
	private _shouldPlaySound: boolean = $state(true);

	constructor(chess: Chess, game: Game) {
		this.chess = chess;
		this.game = game;
	}

	// Reactive derivations
	public isPlayerTurn = $derived.by(() => this._positionIndex % 2 === 0);
	public isComplete = $derived.by(() => this._positionIndex >= this.game.moves.length * 2 - 1);
	public canGoBackInGame = $derived.by(() => this._positionIndex > -1);
	public canGoForwardInGame = $derived.by(
		() => this._positionIndex < this.game.moves.length * 2 - 1
	);
	public isInJump = $derived.by(() => this._jumpingIndex != null);
	public canGoBackInJump = $derived.by(() => {
		// Can go back in jump mode if we're not at the first position (-1)
		return this._jumpingIndex > -1;
	});
	public canGoForwardInJump = $derived.by(() => {
		// Can go forward in jump mode if we're not at the last position but make sure we don't let player go beyond the moves that they have played
		return this._jumpingIndex !== null && this._jumpingIndex < this.game.moves.length * 2 - 1;
	});

	// Getters
	public get positionIndex(): number {
		return this._positionIndex;
	}

	public get jumpingIndex(): number | null {
		return this._jumpingIndex;
	}

	public get isProgrammaticMove(): boolean {
		return this._isProgrammaticMove;
	}

	public get shouldPlaySound(): boolean {
		return this._shouldPlaySound;
	}

	public get chessBoard(): Chess {
		return this.chess;
	}

	public get gameData(): Game {
		return this.game;
	}

	// State mutators (for Engine use)
	public setPositionIndex(value: number): void {
		this._positionIndex = value;
	}

	public setJumpingIndex(value: number | null): void {
		this._jumpingIndex = value;
	}

	public setProgrammaticMove(value: boolean): void {
		this._isProgrammaticMove = value;
	}

	public setShouldPlaySound(value: boolean): void {
		this._shouldPlaySound = value;
	}

	// Query methods
	public getExpectedMoveAtPosition(posIndex: number): string | null {
		if (posIndex < 0 || posIndex >= this.game.moves.length * 2) {
			return null;
		}
		const pairIndex = Math.floor(posIndex / 2);
		const isComputerMove = posIndex % 2 === 0;
		const movePair = this.game.moves[pairIndex];
		if (!movePair) return null;
		return isComputerMove ? movePair.computer : movePair.player;
	}

	public getCurrentFEN(): string {
		return this.chess.fen();
	}

	public getMoveHistory(): string[] {
		const moves: string[] = [];
		for (let i = 0; i <= this._positionIndex; i++) {
			const move = this.getExpectedMoveAtPosition(i);
			if (move) moves.push(move);
		}
		return moves;
	}

	public resetToInitialState(): void {
		this.chess.load(this.game.fen);
		this._positionIndex = -1;
		this._jumpingIndex = null;
		this._isProgrammaticMove = false;
		this._shouldPlaySound = true;
	}
}

export const DEFAULT_SETTINGS = {
	computerMoveDelay: 250,
	flipOrientation: false,
	muted: false
};

export class Engine {
	private state: GameState;
	private boardElement: HTMLElement;
	private settings: Partial<typeof DEFAULT_SETTINGS>;

	private onStart?: () => void;
	private onCorrectMove?: (move: Move) => void;
	private onWrongMove?: (move: Move) => void;
	private onEnd?: () => void;
	private onMoveMade?: (move: {
		move: string;
		isComputer: boolean;
		isCorrect?: boolean;
		positionIndex: number;
	}) => void;
	private onJump?: (positionIndex: number) => void;

	constructor(
		chess: Chess,
		game: Game,
		boardElement: HTMLElement,
		settings: Partial<typeof DEFAULT_SETTINGS>,
		callbacks: {
			onStart?: () => void;
			onCorrectMove?: (move: Move) => void;
			onWrongMove?: (move: Move) => void;
			onEnd?: () => void;
			onMoveMade?: (move: {
				move: string;
				isComputer: boolean;
				isCorrect?: boolean;
				positionIndex: number;
			}) => void;
			onJump?: (positionIndex: number) => void;
		}
	) {
		this.state = new GameState(chess, game);
		this.boardElement = boardElement;
		this.settings = { ...DEFAULT_SETTINGS, ...settings };
		this.onStart = callbacks.onStart;
		this.onCorrectMove = callbacks.onCorrectMove;
		this.onWrongMove = callbacks.onWrongMove;
		this.onEnd = callbacks.onEnd;
		this.onMoveMade = callbacks.onMoveMade;
		this.onJump = callbacks.onJump;
	}

	// Expose state for components to read
	public getState(): GameState {
		return this.state;
	}

	// Convenience getters for common state access
	public get isPlayerTurn() {
		return this.state.isPlayerTurn;
	}
	public get isComplete() {
		return this.state.isComplete;
	}
	public get canGoBack() {
		return this.state.canGoBackInGame;
	}
	public get canGoForward() {
		return this.state.canGoForwardInGame;
	}
	public get isInJump() {
		return this.state.isInJump;
	}

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

	public actions = {
		start: async (): Promise<void> => {
			this.onStart?.();
			this.state.resetToInitialState();
			await new Promise((resolve) => setTimeout(resolve, 500));
			await this.actions.playComputerMove();
		},

		handleWrongMove: async (move: Move): Promise<void> => {
			this.onWrongMove?.(move);
			this.onMoveMade?.({
				move: move.lan,
				isComputer: false,
				isCorrect: false,
				positionIndex: this.state.positionIndex + 1
			});
			await this.waitForAnimations();
			setTimeout(() => {
				this.state.chessBoard.undo();
			}, 100);
		},

		handleCorrectMove: async (move: Move): Promise<void> => {
			this.onCorrectMove?.(move);
			this.onMoveMade?.({
				move: move.lan,
				isComputer: false,
				isCorrect: true,
				positionIndex: this.state.positionIndex + 1
			});

			this.state.setPositionIndex(this.state.positionIndex + 1);

			if (this.state.isComplete) {
				this.onEnd?.();
				return;
			}

			await this.waitForAnimations();
			setTimeout(async () => {
				await this.actions.playComputerMove();
			});
		},

		playComputerMove: async (): Promise<void> => {
			if (!this.state.canGoForwardInGame) return;

			const nextMove = this.state.getExpectedMoveAtPosition(this.state.positionIndex + 1);
			if (!nextMove) return;

			await new Promise((resolve) => setTimeout(resolve, this.settings.computerMoveDelay));
			this.state.setProgrammaticMove(true);
			this.state.chessBoard.move(nextMove);

			this.onMoveMade?.({
				move: nextMove,
				isComputer: false,
				isCorrect: false,
				positionIndex: this.state.positionIndex + 1
			});

			await this.waitForAnimations();
			this.state.setPositionIndex(this.state.positionIndex + 1);
			this.state.setProgrammaticMove(false);
		}
	};

	public jump = {
		config: {
			init: () => {
				this.state.setJumpingIndex(this.state.positionIndex);
			},

			teardown: () => {
				this.state.setJumpingIndex(null);
			}
		},

		first: () => {
			if (!this.state.isInJump) this.jump.config.init();
			this.state.setProgrammaticMove(true);
			this.state.chessBoard.load(this.state.gameData.fen);
			this.state.setJumpingIndex(-1);
			this.state.setProgrammaticMove(false);
			this.onJump?.(-1);
		},

		back: () => {
			if (!this.state.isInJump) this.jump.config.init();
			if (this.state.jumpingIndex === -1) return;
			this.state.setProgrammaticMove(true);
			this.state.chessBoard.undo();
			this.state.setJumpingIndex(this.state.jumpingIndex! - 1);
			this.state.setProgrammaticMove(false);
			this.onJump?.(this.state.jumpingIndex!);
		},

		forward: () => {
			if (!this.state.isInJump) return;
			this.state.setProgrammaticMove(true);
			const move = this.state.getExpectedMoveAtPosition(this.state.jumpingIndex! + 1)!;
			this.state.chessBoard.move(move);
			this.state.setJumpingIndex(this.state.jumpingIndex! + 1);
			this.state.setProgrammaticMove(false);
			if (this.state.jumpingIndex === this.state.positionIndex) this.jump.config.teardown();

			this.onJump?.(this.state.jumpingIndex!);
		},

		last: () => {
			if (!this.state.isInJump) return;
			this.state.setProgrammaticMove(true);
			this.state.setShouldPlaySound(false);
			this.state.chessBoard.load(this.state.gameData.fen);
			for (let i = -1; i < this.state.positionIndex - 1; i++) {
				const move = this.state.getExpectedMoveAtPosition(i + 1)!;
				this.state.chessBoard.move(move);
			}
			this.state.setShouldPlaySound(true);
			const move = this.state.getExpectedMoveAtPosition(this.state.positionIndex);
			this.state.chessBoard.move(move!);
			this.state.setProgrammaticMove(false);
			this.jump.config.teardown();
			this.onJump?.(this.state.positionIndex);
		}
	};

	async handleMove(move: Move): Promise<boolean> {
		if (this.state.shouldPlaySound && !this.settings.muted) {
			playSound(!!move.captured);
		}
		if (!this.state.isPlayerTurn || this.state.isProgrammaticMove) {
			return false;
		}
		const expectedMove = this.state.getExpectedMoveAtPosition(this.state.positionIndex + 1);
		if (!expectedMove || move.lan !== expectedMove) {
			await this.actions.handleWrongMove(move);
			return false;
		} else {
			await this.actions.handleCorrectMove(move);
			return true;
		}
	}
}
