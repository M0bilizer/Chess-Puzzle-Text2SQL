import { createRouter } from 'sv-router';
import NotFound from '@/+NotFound.svelte';
import { PUZZLE_ROUTES, puzzleRoutes } from '@/features/puzzle/routes';
import { ABOUT_PATHS, aboutRoutes } from './features/about/route';
import { SETTINGS_PATHS, settingsRoutes } from './features/settings/route';

export const PATHS = {
	...PUZZLE_ROUTES,
	...ABOUT_PATHS,
	...SETTINGS_PATHS
};

// don't export { p }, use Paths instead
export const { navigate, isActive, route } = createRouter({
	...puzzleRoutes,
	...aboutRoutes,
	...settingsRoutes,
	'*': NotFound
});
