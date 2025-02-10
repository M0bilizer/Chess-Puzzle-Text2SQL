import { writable } from 'svelte/store';
import { toast } from '@zerodevx/svelte-toast';

export const congratulationModalState = writable({ open: false });
export const searchModalState = writable({ open: false });

export function closeSearchModal() {
	toast.pop({ target: 'modal' });
	searchModalState.set({ open: false });
}

export function openSearchModal() {
	toast.pop({ target: 'root' });
	searchModalState.set({ open: true });
}
