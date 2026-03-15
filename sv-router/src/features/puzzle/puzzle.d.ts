export type Game = {
	fen: string;
	moves: { computer: string; player: string }[];
};

export interface Puzzle {
	id: number;
	puzzleId: string;
	fen: string;
	moves: string;
	rating: number;
	ratingDeviation: number;
	popularity: number;
	nbPlays: number;
	themes: string;
	gameUrl: string;
	openingTags: string;
}
