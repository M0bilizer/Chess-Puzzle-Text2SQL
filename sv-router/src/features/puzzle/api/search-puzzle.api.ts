import { AsyncResult, Result } from 'typescript-result';
import { IOError } from '@/common/types/error';
import { puzzle } from '@/features/puzzle/api/puzzle-stub';
import type { Puzzle } from '../type.svelte';

export function searchPuzzleApi(search: string): AsyncResult<Puzzle[], IOError> {
	// Stub mode - returns mock data after 1 second delay
	return Result.fromAsync(async () => {
		console.log(`Looking for ${search}`);
		await new Promise((resolve) => setTimeout(resolve, 1000));
		return Result.ok(puzzle);
	});

	// Real API call (commented out for now)
	// return Result.fromAsync(async () => {
	// 	const apiUrl = env.apiUrl;
	// 	const url = `${apiUrl}/puzzles?search=${encodeURIComponent(search)}`;
	//
	// 	const response = await fetch(url, {
	// 		method: 'GET',
	// 		headers: {
	// 			'Content-Type': 'application/json',
	// 			Accept: 'application/json'
	// 		},
	// 		signal: AbortSignal.timeout(10000)
	// 	});
	//
	// 	if (!response.ok) {
	// 		return Result.error(new IOError());
	// 	}
	//
	// 	const data: Puzzle[] = await response.json();
	// 	return Result.ok(data);
	// });
}
