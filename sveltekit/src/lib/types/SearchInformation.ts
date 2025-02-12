import type { PuzzleInstance } from '$lib/types/puzzleInstance';
import type { SearchMetadata } from '$lib/types/SearchMetadata';

export type Search = {
	metadata: SearchMetadata;
	data: PuzzleInstance[];
};
