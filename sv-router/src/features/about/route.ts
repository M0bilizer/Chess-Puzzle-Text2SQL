import About from '@/features/about/+About.svelte';
import AboutLayout from './AboutLayout.svelte';

export const ABOUT_PATHS = {
	ABOUT: '/about'
};

export const aboutRoutes = {
	'/about': {
		'/': About,
		layout: AboutLayout
	}
};
