import NotFound from '@/+NotFound.svelte';
import { puzzleRoutes } from '@/features/puzzle/routes';
import { createRouter } from 'sv-router';

import { aboutRoutes } from './features/about/route';
import { settingsRoutes } from './features/settings/route';

export const { p, navigate, route } = createRouter(
	{
		...puzzleRoutes,
		...aboutRoutes,
		...settingsRoutes,
		'*': NotFound
	},
	{
		base: import.meta.env.VITE_BASE_PATH || ''
	}
);
