import { mount } from 'svelte';
import 'virtual:uno.css'
import './app.css';
import App from './App.svelte';

mount(App, { target: document.querySelector('#app')! });
