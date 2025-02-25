import type { GameProgress } from '$lib/types/gameProgress';
import type { Puzzle } from '$lib/types/puzzle';

export type PuzzleInstance = {
	puzzle: Puzzle;
	progress: GameProgress;
};
