import type { Puzzle } from '../type.svelte';
import { getPlayerColor } from '../utils';
import { playlistCollection } from './playlist-collection.svelte';

export type Playlist = {
	name: string;
	puzzles: { puzzleId: string; fen: string; orientation: 'white' | 'black'; result?: boolean }[];
	currentIndex: number;
};

class CurrentPlaylistStore {
	private getPlaylist: () => Playlist | null;
	public constructor(getFn: () => Playlist | null) {
		this.getPlaylist = getFn;
	}

	private get playlist() {
		return this.getPlaylist();
	}

	public isActive = $derived(this.playlist !== null);
	public currentIndex = $derived(this.playlist?.currentIndex ?? 0);
	public name = $derived(this.playlist?.name ?? null);
	public puzzles = $derived(this.playlist?.puzzles ?? []);
	public currentPuzzle = $derived(this.playlist?.puzzles[this.playlist?.currentIndex ?? 0] ?? null);
	public hasPrev = $derived(this.playlist ? this.playlist.currentIndex > 0 : false);
	public hasNext = $derived(
		this.playlist
			? this.playlist.puzzles.find((it) => it.result === undefined)
				? true
				: false
			: undefined
	);
	public totalPuzzles = $derived(this.playlist?.puzzles.length ?? 0);

	getPrev(id: string) {
		const playlist = this.playlist;
		if (!playlist) throw new Error('No playlist');
		const index = playlist.puzzles.findIndex((it) => it.puzzleId === id);
		if (index === -1) throw new Error('Puzzle not found');
		if (index === 0) return undefined;
		return playlist.puzzles[index - 1];
	}

	getNext(id: string) {
		const playlist = this.playlist;
		if (!playlist) throw new Error('No playlist');
		const index = playlist.puzzles.findIndex((it) => it.puzzleId === id);
		if (index === -1) throw new Error('Puzzle not found');
		if (index === playlist.puzzles.length - 1) return undefined;
		return playlist.puzzles[index + 1];
	}

	init(name: string, data: Puzzle[]) {
		playlistCollection.set({
			name: name,
			puzzles: data.map((it) => ({
				puzzleId: it.puzzleId,
				fen: it.fen,
				orientation: getPlayerColor(it.fen) === 'w' ? 'white' : 'black',
				result: undefined
			})),
			currentIndex: 0
		});
		playlistCollection.setActive(name);
	}

	setPuzzleResult(id: string, result: boolean) {
		const playlist = this.playlist;
		if (!playlist) throw new Error('No playlist');
		const index = playlist.puzzles.findIndex((it) => it.puzzleId === id);
		if (index === -1) throw new Error('Puzzle not found');
		playlist.puzzles[index].result = result;
	}

	getNextPuzzle() {
		const playlist = this.playlist;
		if (!playlist) throw new Error('No playlist');
		return playlist.puzzles.find((it) => it.result === undefined);
	}

	goToPuzzle(index: number) {
		const playlist = this.playlist;
		if (!playlist) throw new Error('No playlist');
		if (index >= 0 && index < playlist.puzzles.length) {
			playlist.currentIndex = index;
		}
	}
}

export const currentPlaylist = new CurrentPlaylistStore(() => playlistCollection.active);
