import { IOError } from '@/common/types/error';
import { IndexedDbCache } from '@/lib/cache';
import { createCacheClient } from '@/lib/fetch';
import { env } from '@/main';
import { AsyncResult, Result } from 'typescript-result';
import type { Puzzle } from '../type.svelte';

export function searchPuzzle(search: string): AsyncResult<Puzzle[], IOError> {
	return Result.fromAsync(async () => {
		const cachedKy = createCacheClient(env.apiUrl, new IndexedDbCache());
		const data = await cachedKy
			.get<Puzzle[]>(`/api/puzzles?search=${encodeURIComponent(search)}`, {
				context: {
					cacheKey: search
				}
			})
			.json();
		return Result.ok(data);
	});
}

export function getPuzzle(id: string): AsyncResult<Puzzle, IOError> {
	return Result.fromAsync(async () => {
		const cachedKy = createCacheClient(env.apiUrl, new IndexedDbCache());
		const data = await cachedKy
			.get<Puzzle>(`/api/puzzles/${id}`, {
				context: {
					cacheKey: id
				}
			})
			.json();
		return Result.ok(data);
	});
}
