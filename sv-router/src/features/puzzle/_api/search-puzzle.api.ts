import { Result } from 'typescript-result';
import type { PuzzleDto } from './puzzle';
import { IOError } from '../../../common/types/error';

export async function searchPuzzleApi(
	search: string,
	baseUrl: string
): Promise<Result<PuzzleDto[], IOError>> {
	const url = `${baseUrl}/puzzles?search=${encodeURIComponent(search)}`;
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
	const data: PuzzleDto[] = await response.json();
	return Result.ok(data);
}
