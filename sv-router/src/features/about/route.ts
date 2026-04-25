import About from '@/features/about/+About.svelte';

export const ABOUT_PATHS = {
	ABOUT: '/about'
};

export const aboutRoutes = {
	'/about': {
		'/': About
	}
};
