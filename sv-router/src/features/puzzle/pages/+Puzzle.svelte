<script lang="ts">
	import Game from '@/features/puzzle/components/Game.svelte';
	import MainWithAsidePage from '@/common/components/MainWithAsidePage.svelte';
	import ChessDescription from '../components/ChessDescription.svelte';
	import { puzzle as puzzleStub } from '../api/puzzle-stub';
	import { puzzleToGame } from '../utils';

	const puzzle = puzzleStub[3];
	const game = puzzleToGame(puzzle);

	let p: Game;
</script>

<MainWithAsidePage class="py-4">
	<main class="space-y-4">
		<Game
			bind:this={p}
			{game}
			onCorrectMove={() => console.log('ok')}
			onWrongMove={() => console.log('oops')}
		/>
		<ChessDescription {puzzle} />
		<!-- <button onclick={() => p.reset()}>reset</button>
		<button onclick={() => p.back()}>back</button>
		<button onclick={() => p.forward()}>forward</button>
		<button onclick={() => p.end()}>end</button> -->
	</main>
</MainWithAsidePage>
