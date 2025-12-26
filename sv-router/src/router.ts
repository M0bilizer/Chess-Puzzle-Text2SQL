import { createRouter } from 'sv-router';
import PuzzlePage from './features/puzzle/routes/PuzzlePage.svelte';
import HomePage from './HomePage.svelte';

export const { p, navigate, isActive, route } = createRouter({
	'/': HomePage,
	'/puzzle': PuzzlePage
});
