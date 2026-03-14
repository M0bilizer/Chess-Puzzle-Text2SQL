export function getPlayerColor(fen: string, flipOrientation: boolean): 'w' | 'b' {
	const fields = fen.split(' ');

	const activeColor = fields[1];

	if (activeColor !== 'w' && activeColor !== 'b') {
		throw new Error('Invalid FEN: Active color must be "w" or "b".');
	}

	const color = activeColor === 'w' ? 'b' : 'w';

	return flipOrientation ? (color === 'w' ? 'b' : 'w') : color;
}
