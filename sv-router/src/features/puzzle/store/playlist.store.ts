import { writable, get } from 'svelte/store';

export type Playlist = {
	name: string;
	puzzles: { puzzleId: string; fen: string; orientation: 'white' | 'black'; result?: boolean }[];
	currentIndex: number;
};

function createPlaylistStore() {
	const { subscribe, set, update } = writable<Playlist | null>(null);

	return {
		subscribe,
		set,
		update,

		getCurrent() {
			const session = get({ subscribe });
			const puzzle = session?.puzzles[session.currentIndex];
			if (!puzzle) throw new Error('No current puzzle');
			return puzzle;
		},

		setCurrentPuzzleResult(result: boolean) {
			update((session) => {
				if (!session) return session;
				const currentPuzzle = session.puzzles[session.currentIndex];
				if (currentPuzzle) {
					currentPuzzle.result = result;
				}
				return session;
			});
		},

		incrementCurrentIndex() {
			update((session) => {
				if (!session) return session;
				if (session.currentIndex < session.puzzles.length - 1) {
					session.currentIndex += 1;
				}
				return session;
			});
		},

		hasNext() {
			const session = get({ subscribe });
			if (!session) return false;
			return session.currentIndex < session.puzzles.length - 1;
		}
	};
}

export const playlistStore = createPlaylistStore();
