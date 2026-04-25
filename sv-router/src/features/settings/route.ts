import SETTINGS from '@/features/settings/+Settings.svelte';

export const SETTINGS_PATHS = {
	SETTINGS: '/settings'
};

export const settingsRoutes = {
	'/settings': {
		'/': SETTINGS
	}
};
