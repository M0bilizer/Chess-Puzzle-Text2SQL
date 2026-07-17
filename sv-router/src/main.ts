import App from '@/App.svelte';
import '@/app.css';
import {
	type Env,
	loadApplicationConfiguration,
	loadConfigFromVite
} from '@/common/config/ApplicationConfig';
import { createStore } from 'idb-keyval';
import ky from 'ky';
import { mount } from 'svelte';

// [FIXME] unnecessary to verify env
export const env: Env = loadApplicationConfiguration(loadConfigFromVite()).getOrElse((issues) => {
	console.error(issues.getFormattedMessages());
	throw new Error('Error loading application configuration.');
});

const api = ky.create({ baseUrl: env.apiUrl });
const searchDb = createStore('search', 'keyval');
const puzzleDb = createStore('puzzleId', 'keyval');

mount(App, { target: document.querySelector('#app')! });

export { api, searchDb, puzzleDb };
