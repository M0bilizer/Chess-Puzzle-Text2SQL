import HomeSvelte from '@/features/puzzle/pages/+Home.svelte';
import Puzzle from '@/features/puzzle/pages/+Puzzle.svelte';
import PuzzleLayout from '@/features/puzzle/PuzzleLayout.svelte';

export const PUZZLE_ROUTES = {
	ROOT: '/',
	PUZZLE: `/puzzle/:puzzleId`,
	SEARCH: `/search`,
	layout: PuzzleLayout
} as const;

export const puzzleRoutes = {
	[PUZZLE_ROUTES.ROOT]: HomeSvelte,
	[PUZZLE_ROUTES.PUZZLE]: Puzzle
};
