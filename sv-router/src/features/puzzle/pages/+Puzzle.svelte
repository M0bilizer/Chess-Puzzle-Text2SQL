<script lang="ts">
	import Game from '@/features/puzzle/components/Game.svelte';
	import MainWithAsidePage from '@/common/components/MainWithAsidePage.svelte';

	const puzzle = {
		fen: '8/4K2k/2P2R2/6pp/8/6P1/2r5/8 b - - 1 53',
		moves: [
			{ computer: 'c2c3', player: 'e7d7' },
			{ computer: 'h5h4', player: 'g3h4' },
			{ computer: 'g5g4', player: 'c6c7' }
		]
	};

	let p: Game;
</script>

<MainWithAsidePage>
	<main>
		<Game
			bind:this={p}
			game={puzzle}
			onCorrectMove={() => console.log('ok')}
			onWrongMove={() => console.log('oops')}
		/>
		<button onclick={() => p.reset()}>reset</button>
		<button onclick={() => p.back()}>back</button>
		<button onclick={() => p.forward()}>forward</button>
		<button onclick={() => p.end()}>end</button>
	</main>
</MainWithAsidePage>
