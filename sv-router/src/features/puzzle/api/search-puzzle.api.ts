import { AsyncResult, Result } from 'typescript-result';
import { IOError } from '@/common/types/error';
import type { Puzzle } from '../type.svelte';
import { env } from '@/main';

export function searchPuzzleApi(search: string): AsyncResult<Puzzle[], IOError> {
	// return Result.fromAsync(async () => {
	// 	console.log(`Looking for ${search}`);
	// 	await new Promise((resolve) => setTimeout(resolve, 1000));
	// 	return Result.ok(puzzle);
	// });
	return Result.fromAsync(async () => {
		const apiUrl = env.apiUrl;
		const url = `${apiUrl}/puzzles?search=${encodeURIComponent(search)}`;

		const response = await fetch(url, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json',
				Accept: 'application/json'
			},
			signal: AbortSignal.timeout(10000)
		});

		if (!response.ok) {
			return Result.error(new IOError());
		}

		const data: Puzzle[] = await response.json();
		return Result.ok(data);
	});
}
