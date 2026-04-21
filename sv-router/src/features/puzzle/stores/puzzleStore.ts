import { derived, type Readable, writable } from 'svelte/store';
import type { Puzzle } from '@/features/puzzle/type';

export type PuzzleStore = ReturnType<typeof createPuzzleStore>;

export function createPuzzleStore(initialPuzzles: Puzzle[] = []) {
	const { subscribe, set, update } = writable<Puzzle[]>(initialPuzzles);

	return {
		subscribe,
		set: (puzzles: Puzzle[]) => set(puzzles),
		clear: () => set([]),
		add: (puzzle: Puzzle) => update((p) => [...p, puzzle]),
		remove: (puzzleId: string) => update((p) => p.filter((p) => p.puzzleId !== puzzleId)),
		updatePuzzle: (puzzleId: string, updates: Partial<Puzzle>) =>
			update((puzzles) => puzzles.map((p) => (p.puzzleId === puzzleId ? { ...p, ...updates } : p))),

		map: derived({ subscribe }, ($puzzles) => new Map($puzzles.map((p) => [p.puzzleId, p]))),
		byId: (id: string): Readable<Puzzle | undefined> =>
			derived({ subscribe }, ($puzzles) => $puzzles.find((p) => p.puzzleId === id)),
		stats: derived({ subscribe }, ($puzzles) => ({
			total: $puzzles.length
		}))
	};
}

export const puzzleStore = createPuzzleStore();
export const searchResultsStore = createPuzzleStore();
export const bookmarkedStore = createPuzzleStore();
