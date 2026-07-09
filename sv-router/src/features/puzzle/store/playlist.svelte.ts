import { PersistedState } from "runed";

export type Playlist = {
	name: string;
	puzzles: { puzzleId: string; fen: string; orientation: 'white' | 'black'; result?: boolean }[];
	currentIndex: number;
};

class PlaylistStore {
	private state = new PersistedState<Playlist | null>("playlist", null);
	public isActive = $derived(this.state.current !== null);
  public currentIndex = $derived(this.state.current?.currentIndex ?? null);
  public name = $derived(this.state.current?.name ?? null);
  public puzzles = $derived(this.state.current?.puzzles ?? []);
  public currentPuzzle = $derived(this.state.current?.puzzles[this.state.current?.currentIndex ?? 0]);
	public hasNext = $derived(
		this.state.current
		? this.state.current.currentIndex < this.state.current.puzzles.length - 1
			: false
	);
	public totalPuzzles = $derived(this.state.current?.puzzles.length ?? 0);


	set(playlist: Playlist | null) {
		this.state.current = playlist;
	}

	setCurrentPuzzleResult(result: boolean) {
		if (!this.state.current) throw new Error('No playlist');
		const currentPuzzle = this.state.current.puzzles[this.state.current.currentIndex];
		if (currentPuzzle) {
			currentPuzzle.result = result;
		}
	}

	incrementCurrentIndex() {
		if (!this.state.current) throw new Error('No playlist');
		if (this.state.current.currentIndex < this.state.current.puzzles.length - 1) {
		this.state.current.currentIndex += 1;
		}
	}

	decrementCurrentIndex() {
		if (!this.state.current) throw new Error('No playlist');
		if (this.state.current.currentIndex > 0) {
		this.state.current.currentIndex -= 1;
		}
	}

	goToPuzzle(index: number) {
		if (!this.state.current) throw new Error('No playlist');
		if (index >= 0 && index < this.state.current.puzzles.length) {
		this.state.current.currentIndex = index;
		}
	}

}

export const playlist = new PlaylistStore();
