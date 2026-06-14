import { IOError } from '@/common/types/error';
import { type HttpClient } from '@/lib/fetch';
import { AsyncResult, Result } from 'typescript-result';
import type { Puzzle } from '../type.svelte';

export function searchPuzzle(client: HttpClient, search: string): AsyncResult<Puzzle[], IOError> {
	return Result.fromAsync(async () => {
		const data = await client.get<Puzzle[]>(`/api/puzzles?search=${encodeURIComponent(search)}`, {
			context: {
				cacheKey: search
			}
		});
		return Result.ok(data);
	});
}

export function getPuzzle(client: HttpClient, id: string): AsyncResult<Puzzle, IOError> {
	return Result.fromAsync(async () => {
		const data = await client.get<Puzzle>(`/api/puzzles/${id}`, {
			context: {
				cacheKey: id
			}
		});
		return Result.ok(data);
	});
}
