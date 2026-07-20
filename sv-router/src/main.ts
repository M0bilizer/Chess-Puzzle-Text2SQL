import App from '@/App.svelte';
import '@/app.css';
import { createStore } from 'idb-keyval';
import ky from 'ky';
import { mount } from 'svelte';

const api = ky.create({ baseUrl: import.meta.env.VITE_API_URL });
const searchDb = createStore('search', 'keyval');
const puzzleDb = createStore('puzzleId', 'keyval');

mount(App, { target: document.querySelector('#app')! });

export { api, puzzleDb, searchDb };
