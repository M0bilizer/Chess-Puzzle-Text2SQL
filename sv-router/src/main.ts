import '@/app.css';
import { mount } from 'svelte';
import App from '@/App.svelte';
import {
	type Env,
	loadApplicationConfiguration,
	loadConfigFromVite
} from '@/common/config/ApplicationConfig';
import { IndexedDbCache } from './lib/cache';
import { createCacheClient } from './lib/fetch';

// [FIXME] unnecessary to verify env
export const env: Env = loadApplicationConfiguration(loadConfigFromVite()).getOrElse((issues) => {
	console.error(issues.getFormattedMessages());
	throw new Error('Error loading application configuration.');
});

const cache = new IndexedDbCache();
const httpClient = createCacheClient(env.apiUrl, cache);

mount(App, { target: document.querySelector('#app')! });

export { cache, httpClient };
