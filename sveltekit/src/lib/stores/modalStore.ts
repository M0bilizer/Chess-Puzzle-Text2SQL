import { writable } from 'svelte/store';
import { toast } from '@zerodevx/svelte-toast';

export const congratulationModalState = writable({ open: false });
const _searchModalState = writable({ open: false });

function _closeSearchModal() {
	toast.pop({ target: 'modal' });
	_searchModalState.set({ open: false });
}

function _openSearchModal() {
	toast.pop({ target: 'root' });
	_searchModalState.set({ open: true });
}

export const searchModalState = {
	subscribe: _searchModalState.subscribe,
	close: _closeSearchModal,
	open: _openSearchModal
};
