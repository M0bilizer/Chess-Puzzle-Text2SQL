import { createRouter } from 'sv-router';
import DesignPage from '@/features/design/pages/DesignPage.svelte';
import NotFound from '@/NotFound.svelte';
import PuzzleLayout from '@/features/puzzle/PuzzleLayout.svelte';
import { PUZZLE_ROUTES, puzzleRoutes } from '@/features/puzzle/routes';

export const ROUTES = {
	...PUZZLE_ROUTES,
	DESIGN: '/design'
} as const;

export const { p, navigate, isActive, route } = createRouter({
	...puzzleRoutes,
	[ROUTES.DESIGN]: DesignPage,
	'*': NotFound,
	layout: PuzzleLayout
});
