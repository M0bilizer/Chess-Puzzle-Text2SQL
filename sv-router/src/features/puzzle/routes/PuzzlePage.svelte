<script lang="ts">
	import Puzzle from '../components/Puzzle.svelte';

	const puzzle = {
		fen: '6r1/n7/4pk2/2PpR1p1/1K1P2P1/4BP2/8/8 w - - 2 61',
		moves: [
			{
				computer: 'e3d2',
				player: 'a7c6'
			},
			{ computer: 'b4c3', player: 'c6e5' }
		]
	};
</script>

<Puzzle {puzzle} />
