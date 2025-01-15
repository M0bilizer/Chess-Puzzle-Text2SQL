// src/stores/apiStore.js
import { writable } from 'svelte/store';
import { KOTLIN_SPRING_URL } from '$lib/constants/endpoints';
import { toast } from 'svelte-sonner';
import type { PuzzleType } from '$lib/types/puzzle';

const initialState: { puzzleData: PuzzleType[]; loading: boolean; error: any } = {
	puzzleData: [],
	loading: false,
	error: null
};

const apiStore = writable(initialState);

async function fetchPuzzle(query: string): Promise<boolean> {
	try {
		apiStore.update((state) => ({ ...state, loading: true, error: null }));

		const res = await fetch(`${KOTLIN_SPRING_URL}/debug/db`, {
			method: 'GET'
			// headers: { 'Content-Type': 'application/json' },
			// body: JSON.stringify({ query })
		});

		if (!res.ok) throw new Error(`HTTP error! Status: ${res.status}`);

		const responseData = await res.json();
		if (responseData.status !== 'success') {
			throw new Error(`API error: ${responseData.status}`);
		}

		console.log(responseData.data);
		apiStore.update((state) => ({ ...state, puzzleData: responseData.data, loading: false }));
		return true;
	} catch (error) {
		apiStore.update((state) => ({ ...state, error: error.message, loading: false }));
		toast(error.message);
		return false;
	}
}

function resetStore() {
	apiStore.set(initialState);
}

export default {
	subscribe: apiStore.subscribe,
	fetchPuzzle,
	resetStore
};
