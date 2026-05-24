import { writable } from 'svelte/store';

const initialMode = localStorage.getItem('mode') || 'dark';
export const isDarkMode = writable(initialMode === 'dark');

export function setTheme(isDark: boolean) {
	const mode = isDark ? 'dark' : 'light';
	document.documentElement.setAttribute('data-mode', mode);
	localStorage.setItem('mode', mode);
	isDarkMode.set(isDark);
}

setTheme(initialMode === 'dark');
