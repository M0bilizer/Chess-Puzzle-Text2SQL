import Home from '@/features/puzzle/pages/+Home.svelte';
import Puzzle from '@/features/puzzle/pages/+Puzzle.svelte';

export const PUZZLE_ROUTES = {
	HOME: '/',
	PUZZLE: `/puzzle/:puzzleId`,
	SEARCH: `/search`
} as const;

export const puzzleRoutes = {
	[PUZZLE_ROUTES.HOME]: Home,
	[PUZZLE_ROUTES.PUZZLE]: Puzzle
};
