// src/stores/currentPuzzles.js
import { writable } from 'svelte/store';
import { KOTLIN_SPRING_URL } from '$lib/constants/endpoints';
import type { Puzzle } from '$lib/types/puzzle';
import { toast } from 'svelte-sonner';

interface CurrentPuzzleState {
	puzzles: Puzzle[];
	currentPuzzle: number;
}

export const currentPuzzles = writable<CurrentPuzzleState>({ puzzles: [], currentPuzzle: 0 });
export const pastPuzzles = writable<Puzzle[][]>([]);
export const isLoading = writable<boolean>(false);

export async function searchPuzzles(query: string, debug: boolean = false): Promise<boolean> {
	try {
		isLoading.set(true);

		let res: Response;
		if (!debug) {
			res = await fetch(`${KOTLIN_SPRING_URL}/queryPuzzle`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ query })
			});
		} else {
			res = await fetch(`${KOTLIN_SPRING_URL}/debug/db`, {
				method: 'GET'
			});
		}

		if (!res.ok) throw new Error(`HTTP error! Status: ${res.status}`);

		const responseData = await res.json();
		if (responseData.status !== 'success') {
			throw new Error(`API error: ${responseData.status}`);
		}

		const newPuzzles: Puzzle[] = responseData.data;

		currentPuzzles.set({ puzzles: newPuzzles, currentPuzzle: 0 });
		pastPuzzles.update((pastResults) => [...pastResults, newPuzzles]);
		isLoading.set(false);
		return true;
	} catch (error) {
		console.error('Search failed:', error);
		toast.message(error.message);
		isLoading.set(false);
		return false;
	}
}
