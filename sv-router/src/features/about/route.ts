import About from '@/features/about/+About.svelte';

import AboutLayout from './AboutLayout.svelte';

export const aboutRoutes = {
	'/about': {
		'/': About,
		layout: AboutLayout
	}
};
