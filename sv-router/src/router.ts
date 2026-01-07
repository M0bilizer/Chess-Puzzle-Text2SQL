import { createRouter } from 'sv-router';
import PuzzlePage from './features/puzzle/routes/PuzzlePage.svelte';
import HomePage from './HomePage.svelte';
import DesignPage from './features/design/DesignPage.svelte';

export const { p, navigate, isActive, route } = createRouter({
	'/': HomePage,
	'/puzzle': PuzzlePage,
	'/design': DesignPage
});
