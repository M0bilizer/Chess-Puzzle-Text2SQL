import { Result } from 'typescript-result';
import type { Puzzle } from '@/features/puzzle/puzzle';
import { IOError } from '@/common/types/error';
import { env } from '@/main';

export async function searchPuzzleApi(search: string): Promise<Result<Puzzle[], IOError>> {
	const apiUrl = env.apiUrl;
	const url = `${apiUrl}/puzzles?search=${encodeURIComponent(search)}`;
	const response = await fetch(url, {
		method: 'GET',
		headers: {
			'Content-Type': 'application/json',
			Accept: 'application/json'
		},
		signal: AbortSignal.timeout(10000) // 10 second timeout
	});
	if (!response.ok) {
		return Result.error(new IOError());
	}
	const data: Puzzle[] = await response.json();
	return Result.ok(data);
}
