import { IOError } from '@/common/types/error';
import { AsyncResult, Result } from 'typescript-result';
import type { Puzzle } from '../type.svelte';
import { get, set, setMany } from 'idb-keyval';
import { api, puzzleDb, searchDb } from '@/main';

export function searchPuzzle(search: string): AsyncResult<Puzzle[], IOError> {
	return Result.fromAsync(async () => {
		const cached = await get<Puzzle[]>(search, searchDb);
		if (cached) {
			return Result.ok(cached);
		} else {
			const data = await api
				.get<Puzzle[]>(`/api/puzzles?search=${encodeURIComponent(search)}`)
				.json();
			set(search, data, searchDb);
			setMany(
				data.map((it) => [it.puzzleId, it]),
				puzzleDb
			);
			return Result.ok(data);
		}
	});
}

export function getPuzzle(id: string): AsyncResult<Puzzle, IOError> {
	return Result.fromAsync(async () => {
		const cached = await get<Puzzle>(id, puzzleDb);
		if (cached) {
			return Result.ok(cached);
		} else {
			const data = await api
				.get<Puzzle>(`/api/puzzles/${id}`, {
					context: {
						cacheKey: id
					}
				})
				.json();
			set(id, data, puzzleDb);
			return Result.ok(data);
		}
	});
}
