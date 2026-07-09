export type Playlist = {
	name: string;
	puzzles: { puzzleId: string; fen: string; orientation: 'white' | 'black'; result?: boolean }[];
	currentIndex: number;
};

class PlaylistStore {
	private state = $state<{ playlist: Playlist | null }>({
		playlist: null
  });
	public isActive = $derived(this.state.playlist !== null);
  public currentIndex = $derived(this.state.playlist?.currentIndex ?? null);
	public name = $derived(this.state.playlist?.name ?? null);
	public puzzles = $derived(this.state.playlist?.puzzles ?? []);
	public currentPuzzle = $derived(this.state.playlist?.puzzles[this.state.playlist?.currentIndex ?? 0]);
	public hasNext = $derived(
		this.state.playlist
			? this.state.playlist.currentIndex < this.state.playlist.puzzles.length - 1
			: false
	);
	public totalPuzzles = $derived(this.state.playlist?.puzzles.length ?? 0);


	set(playlist: Playlist | null) {
		this.state.playlist = playlist;
	}

	setCurrentPuzzleResult(result: boolean) {
		if (!this.state.playlist) throw new Error('No playlist');
		const currentPuzzle = this.state.playlist.puzzles[this.state.playlist.currentIndex];
		if (currentPuzzle) {
			currentPuzzle.result = result;
		}
	}

	incrementCurrentIndex() {
		if (!this.state.playlist) throw new Error('No playlist');
		if (this.state.playlist.currentIndex < this.state.playlist.puzzles.length - 1) {
			this.state.playlist.currentIndex += 1;
		}
	}

	decrementCurrentIndex() {
		if (!this.state.playlist) throw new Error('No playlist');
		if (this.state.playlist.currentIndex > 0) {
			this.state.playlist.currentIndex -= 1;
		}
	}

	goToPuzzle(index: number) {
		if (!this.state.playlist) throw new Error('No playlist');
		if (index >= 0 && index < this.state.playlist.puzzles.length) {
			this.state.playlist.currentIndex = index;
		}
	}

}

export const playlist = new PlaylistStore();
