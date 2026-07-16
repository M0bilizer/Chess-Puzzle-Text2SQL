import { Chess, type Move } from 'chess.js';
import { getPlayerColor } from './utils';
import { StateHistory } from 'runed';

export type Game = {
	fen: string;
	moves: { computer: string; player: string }[];
};

/* This is the dto from the backend */
export type Puzzle = {
	puzzleId: string;
	fen: string;
	moves: string;
	rating: number;
	ratingDeviation: number;
	popularity: number;
	nbPlays: number;
	themes: string;
	gameUrl: string;
	openingTags: string | null;
};

/* This acts as the move validation, kinda like a clientside dto */
export class PuzzleEngine {
	private solutionMoves: string[];

	constructor(puzzle: Puzzle) {
		this.solutionMoves = puzzle.moves.trim().split(/\s+/);
	}

	getSolutionMoveAt(index: number): string | undefined {
		return this.solutionMoves[index] || undefined;
	}

	getTotalMoves(): number {
		return this.solutionMoves.length;
	}

	validateMove(move: Move, expectedMove: string): boolean {
		const uciMove = `${move.from}${move.to}${move.promotion || ''}`;
		return uciMove === expectedMove;
	}
}

export class PuzzleGame {
	private engine: PuzzleEngine;
	private fens: string[];
	private playerColor: 'w' | 'b';

	private movePlayed = $state<{
		index: number;
		move: Move;
		isComputer: boolean;
		isCorrect: boolean;
	}>();

	private _movesPlayed: StateHistory<
		| {
				index: number;
				move: Move;
				isComputer: boolean;
				isCorrect: boolean;
		  }
		| undefined
	>;

	public currentIndex = $state(0);
	public latestIndex = $state(0);

	constructor(puzzle: Puzzle) {
		this.engine = new PuzzleEngine(puzzle);
		this.fens = this.computeAllFens(puzzle, this.engine);
		this.playerColor = getPlayerColor(puzzle.fen);
		this.movePlayed = undefined;
		this._movesPlayed = new StateHistory(
			() => this.movePlayed,
			(mP) => (this.movePlayed = mP)
		);
	}

	private computeAllFens(puzzle: Puzzle, engine: PuzzleEngine): string[] {
		const chess = new Chess(puzzle.fen);
		const fens: string[] = [puzzle.fen];

		for (let i = 0; i < engine.getTotalMoves(); i++) {
			const move = engine.getSolutionMoveAt(i)!;
			chess.move(move);
			fens.push(chess.fen());
		}
		return fens;
	}

	getTotalMoves(): number {
		return this.engine.getTotalMoves();
	}

	getPlayerColor(): 'w' | 'b' {
		return this.playerColor;
	}

	getFenAt(index: number): string {
		if (index < 0 || index >= this.fens.length) {
			throw new Error(`Index ${index} is out of bounds`);
		}
		return this.fens[index];
	}

	getCorrectMoveAt(index: number): Move {
		if (index < 0 || index >= this.fens.length) {
			throw new Error(`Index ${index} is out of bounds`);
		}
		const chess = new Chess(this.fens[index]);
		const move = chess.move(this.engine.getSolutionMoveAt(index)!);
		return move;
	}

	public get movesPlayed(): StateHistory<
		| {
				index: number;
				move: Move;
				isComputer: boolean;
				isCorrect: boolean;
		  }
		| undefined
	> {
		return this._movesPlayed;
	}

	makeMove(index: number, move: Move): boolean {
		const expectedMove = this.engine.getSolutionMoveAt(index);
		if (!expectedMove) {
			return false;
		}
		const isCorrect = this.engine.validateMove(move, expectedMove);
		if (!isCorrect) {
			this.movePlayed = {
				index: this.currentIndex,
				move: move,
				isComputer: false,
				isCorrect: false
			};
			this.currentIndex++;
			return false;
		}
		this.movePlayed = {
			index: this.currentIndex,
			move: move,
			isComputer: false,
			isCorrect: true
		};
		this.currentIndex++;
		this.latestIndex++;
		return true;
	}

	fenAt(index: number): string {
		if (index < 0 || index >= this.fens.length) {
			throw new Error(`Index ${index} is out of bounds`);
		}
		return this.fens[index];
	}
}
