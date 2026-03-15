import '@/app.css';
import { mount } from 'svelte';
import App from '@/App.svelte';
import {
	type Env,
	loadApplicationConfiguration,
	loadConfigFromVite
} from '@/common/config/ApplicationConfig';

export const env: Env = loadApplicationConfiguration(loadConfigFromVite()).getOrElse((issues) => {
	console.error(issues.getFormattedMessages());
	throw new Error('Error loading application configuration.');
});
mount(App, { target: document.querySelector('#app')! });
