import type { Puzzle } from '$lib/types/puzzle';

export type Search = {
	query: string;
	result: Puzzle[];
};
