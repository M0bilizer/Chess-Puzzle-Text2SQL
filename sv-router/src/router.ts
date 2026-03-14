import { createRouter } from 'sv-router';
import Home from './features/puzzle/Home.svelte';
import DesignPage from './features/design/pages/DesignPage.svelte';
import Layout from './Layout.svelte';
import './app.css';
import NotFound from './NotFound.svelte';
import Search from './features/puzzle/Search.svelte';
import Puzzle from './features/puzzle/Puzzle.svelte';

export const ROUTES = {
	HOME: '/',
	PUZZLE: `/puzzle/:puzzleId`,
	SEARCH: `/search`,
	DESIGN: '/design'
} as const;

export const { p, navigate, isActive, route } = createRouter({
	[ROUTES.HOME]: Home,
	[ROUTES.PUZZLE]: Puzzle,
	[ROUTES.SEARCH]: Search,
	[ROUTES.DESIGN]: DesignPage,
	'*': NotFound,
	layout: Layout
});
