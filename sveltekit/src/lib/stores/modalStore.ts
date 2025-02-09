import { writable } from 'svelte/store';

export const congratulationModalState = writable({ open: false });
export const searchModalState = writable({ open: false });
