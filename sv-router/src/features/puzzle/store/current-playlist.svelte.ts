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
	public currentIndex = $derived.by(() => {
		console.log(this.playlist?.currentIndex);
		return this.playlist?.currentIndex ?? 0;
	});
	public name = $derived(this.playlist?.name ?? null);
	public puzzles = $derived(this.playlist?.puzzles ?? []);
	public currentPuzzle = $derived(this.playlist?.puzzles[this.playlist?.currentIndex ?? 0] ?? null);
	public hasNext = $derived(
		this.playlist ? this.playlist.currentIndex < this.playlist.puzzles.length - 1 : false
	);
	public totalPuzzles = $derived(this.playlist?.puzzles.length ?? 0);

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

	setCurrentPuzzleResult(result: boolean) {
		const playlist = this.playlist;
		if (!playlist) throw new Error('No playlist');
		const currentPuzzle = playlist.puzzles[playlist.currentIndex];
		if (currentPuzzle) {
			currentPuzzle.result = result;
		}
	}

	incrementCurrentIndex() {
		const playlist = this.playlist;
		if (!playlist) throw new Error('No playlist');
		if (playlist.currentIndex < playlist.puzzles.length - 1) {
			playlist.currentIndex += 1;
		}
	}

	decrementCurrentIndex() {
		const playlist = this.playlist;
		if (!playlist) throw new Error('No playlist');
		if (playlist.currentIndex > 0) {
			playlist.currentIndex -= 1;
		}
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
