<script lang="ts">
	import Puzzle from '@/features/puzzle/components/Puzzle.svelte';

	const puzzle = {
		fen: '8/4K2k/2P2R2/6pp/8/6P1/2r5/8 b - - 1 53',
		moves: [
			{ computer: 'c2c3', player: 'e7d7' },
			{ computer: 'h5h4', player: 'g3h4' },
			{ computer: 'g5g4', player: 'c6c7' }
		]
	};

	let p: Puzzle;
	let c: Puzzle;
</script>

<div class="container">
	<Puzzle
		bind:this={p}
		{puzzle}
		onCorrectMove={() => console.log('ok')}
		onWrongMove={() => console.log('oops')}
	/>
	<button onclick={() => p.reset()}>reset</button>
	<button onclick={() => p.back()}>back</button>
	<button onclick={() => p.forward()}>forward</button>
	<button onclick={() => p.end()}>end</button>
	<Puzzle
		bind:this={c}
		settings={{
			computerMoveDelay: 500,
			flipOrientation: true
		}}
		{puzzle}
		onCorrectMove={() => console.log('ok')}
		onWrongMove={() => console.log('oops')}
	/>
</div>
