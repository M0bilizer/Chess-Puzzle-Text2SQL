import PuzzleLayout from '@/features/puzzle/PuzzleLayout.svelte';
import Puzzle from '@/features/puzzle/pages/+Puzzle.svelte';

import Home from './pages/+Home.svelte';

export const puzzleRoutes = {
	'/': {
		'/': Home
	},
	'/puzzle': {
		'/:id': Puzzle,
		layout: PuzzleLayout
	}
};
