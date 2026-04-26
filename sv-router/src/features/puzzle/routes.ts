import Home from '@/features/puzzle/pages/+Home.svelte';
import Puzzle from '@/features/puzzle/pages/+Puzzle.svelte';
import PuzzleLayout from '@/features/puzzle/PuzzleLayout.svelte';

export const PUZZLE_ROUTES = {
	HOME: '/',
	PUZZLE: (puzzleId: string) => `/puzzle/${puzzleId}`
};

export const puzzleRoutes = {
	'/': {
		layout: PuzzleLayout,
		'/': Home,
		'/puzzle/:id': Puzzle
	}
};
