import Puzzle from '@/features/puzzle/pages/+Puzzle.svelte';
import PuzzleLayout from '@/features/puzzle/PuzzleLayout.svelte';
import Home from './pages/+Home.svelte';

export const PUZZLE_ROUTES = {
	HOME: '/',
	PUZZLE: (puzzleId: string) => `/puzzle/${puzzleId}`
};

export const puzzleRoutes = {
	'/': {
		layout: PuzzleLayout,
		'/': Home
	},
	'/puzzle': {
		'/:id': Puzzle,
		layout: PuzzleLayout
	}
};
