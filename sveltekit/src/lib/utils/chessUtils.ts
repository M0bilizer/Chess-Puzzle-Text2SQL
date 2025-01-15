export function getActiveColor(fen: string): 'w' | 'b' {
	const fields = fen.split(' ');

	const activeColor = fields[1];

	if (activeColor !== 'w' && activeColor !== 'b') {
		throw new Error('Invalid FEN: Active color must be "w" or "b".');
	}

	return activeColor;
}
