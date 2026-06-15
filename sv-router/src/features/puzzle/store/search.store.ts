import { writable } from 'svelte/store';
import type { Puzzle } from '../type.svelte';

export const searchStore = writable<{ search: string; result: Puzzle[] }>({
	search: '',
	result: []
});
