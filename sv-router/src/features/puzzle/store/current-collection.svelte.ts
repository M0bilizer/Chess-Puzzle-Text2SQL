import type { Puzzle } from '../type.svelte';
import { getPlayerColor } from '../utils';
import { collections } from './collections.svelte';

export type Collection = {
	name: string;
	puzzles: { puzzleId: string; fen: string; orientation: 'white' | 'black'; result?: boolean }[];
	currentIndex: number;
};

// TODO make this code more functional to make it cleaner
class CurrentCollectionStore {
	private getCollection: () => Collection | null;
	public constructor(getFn: () => Collection | null) {
		this.getCollection = getFn;
	}

	private get collection() {
		return this.getCollection();
	}

	public isActive = $derived(this.collection !== null);
	public name = $derived(this.collection?.name ?? null);
	public puzzles = $derived(this.collection?.puzzles ?? []);
	public totalPuzzles = $derived(this.collection?.puzzles.length ?? 0);

	init(name: string, data: Puzzle[]) {
		collections.set({
			name: name,
			puzzles: data.map((it) => ({
				puzzleId: it.puzzleId,
				fen: it.fen,
				orientation: getPlayerColor(it.fen) === 'w' ? 'white' : 'black',
				result: undefined
			})),
			currentIndex: 0
		});
		collections.setActive(name);
	}

	setPuzzleResult(id: string, result: boolean) {
		const playlist = this.collection;
		if (!playlist) throw new Error('No playlist');
		const index = playlist.puzzles.findIndex((it) => it.puzzleId === id);
		if (index === -1) throw new Error('Puzzle not found');
		playlist.puzzles[index].result = result;
	}

	public prev(id: string) {
		const playlist = this.collection;
		if (!playlist) return undefined;
		const current = playlist.puzzles.findIndex((it) => it.puzzleId === id);
		if (current === -1) return undefined;
		return playlist.puzzles[current - 1];
	}

	public next(id: string) {
		const playlist = this.collection;
		if (!playlist) return undefined;
		const current = playlist.puzzles.findIndex((it) => it.puzzleId === id);
		if (current === -1) return undefined;
		return playlist.puzzles[current + 1];
	}

	public getFirstUnsolved() {
		const playlist = this.collection;
		if (!playlist) return undefined;
		return playlist.puzzles.find((it) => it.result === undefined);
	}
}

export const currentCollection = new CurrentCollectionStore(() => collections.active);
