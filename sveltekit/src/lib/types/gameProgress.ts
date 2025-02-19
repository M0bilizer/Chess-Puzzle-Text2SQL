export type GameProgress = {
	fen: string;
	orientation: 'w' | 'b';
	moves: string[];
	moveIndex: number;
	hasWon: boolean;
};
