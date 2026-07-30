import SETTINGS from '@/features/settings/+Settings.svelte';

import SettingLayout from './SettingLayout.svelte';

export const settingsRoutes = {
	'/settings': {
		layout: SettingLayout,
		'/': SETTINGS
	}
};
