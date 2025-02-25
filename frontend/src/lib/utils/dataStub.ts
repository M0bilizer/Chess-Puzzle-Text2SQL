import type { Puzzle } from '$lib/types/puzzle';

function shuffleArray<T>(array: T[]): T[] {
	for (let i = array.length - 1; i > 0; i--) {
		const j = Math.floor(Math.random() * (i + 1));
		[array[i], array[j]] = [array[j], array[i]];
	}
	return array;
}

export const originalDataStub: Puzzle[] = [
	{
		id: 2166004,
		puzzleId: 'WWII1',
		fen: '6r1/n7/4pk2/2PpR1p1/1K1P2P1/4BP2/8/8 w - - 2 61',
		moves: 'e3d2 a7c6 b4c3 c6e5',
		rating: 1160,
		ratingDeviation: 263,
		popularity: 57,
		nbPlays: 7,
		themes: 'crushing endgame fork short',
		gameUrl: 'https://lichess.org/Mm27yWb5#121',
		openingTags: ''
	},
	{
		id: 2166005,
		puzzleId: 'WWIIj',
		fen: '8/4K2k/2P2R2/6pp/8/6P1/2r5/8 b - - 1 53',
		moves: 'c2c3 e7d7 h5h4 g3h4 g5g4 c6c7',
		rating: 2158,
		ratingDeviation: 77,
		popularity: 74,
		nbPlays: 90,
		themes: 'advancedPawn crushing endgame long rookEndgame',
		gameUrl: 'https://lichess.org/e54v2vsz/black#106',
		openingTags: ''
	},
	{
		id: 2166006,
		puzzleId: 'WWIKh',
		fen: '1b2r1k1/1b4pp/p7/1p2N2q/1P6/PQ3P2/2r3P1/1R2R1K1 b - - 7 36',
		moves: 'g8f8 e5d7',
		rating: 1059,
		ratingDeviation: 79,
		popularity: 93,
		nbPlays: 1069,
		themes: 'mate mateIn1 middlegame oneMove',
		gameUrl: 'https://lichess.org/MdU0dabe/black#72',
		openingTags: ''
	},
	{
		id: 2166007,
		puzzleId: 'WWITX',
		fen: '2r1b2k/2r3p1/1b2p3/6p1/p1NP4/4P3/5PPP/1RR3K1 w - - 0 36',
		moves: 'c4b6 c7c1 b1c1 c8c1',
		rating: 446,
		ratingDeviation: 87,
		popularity: 100,
		nbPlays: 49,
		themes: 'backRankMate endgame mate mateIn2 short',
		gameUrl: 'https://lichess.org/ZmukJJJ1#71',
		openingTags: ''
	},
	{
		id: 2166008,
		puzzleId: 'WWITd',
		fen: 'r4rk1/1p1b2pp/p1n1p3/2ppP1n1/P6q/2P2P2/1P2B1PP/RNBQ1RK1 w - - 1 15',
		moves: 'c1g5 h4g5',
		rating: 1070,
		ratingDeviation: 326,
		popularity: -100,
		nbPlays: 1,
		themes: 'advantage middlegame oneMove',
		gameUrl: 'https://lichess.org/DuntE20T#29',
		openingTags: 'French_Defense French_Defense_Advance_Variation'
	}
];

export function getDataStub(): Puzzle[] {
	return shuffleArray([...originalDataStub]);
}
