<script lang="ts">
	import type { Move } from 'chess.js';
	import { onMount } from 'svelte';
	import { Chess } from 'svelte-chess';
	import type { Game } from '../types/puzzle';
	import { getFirstMoveColor } from '../utils/getOrientation';

	interface Props {
		puzzle: Game;
	}
	let { puzzle }: Props = $props();

	let boardElement: HTMLElement;
	let chess: Chess;
	let isPlayerMove = $state(false);
	let index = $state(0);

	onMount(() => {
		chess.load(puzzle.fen);
	});

	async function start() {
		await waitForAnimations();
		chess.move(puzzle.moves[index].computer);
		isPlayerMove = true;
	}

	function waitForAnimations() {
		return new Promise<void>((resolve) => {
			const checkAnimations = () => {
				const hasAnimation = boardElement.querySelector('.anim');
				if (!hasAnimation) {
					resolve();
				} else {
					requestAnimationFrame(checkAnimations);
				}
			};
			requestAnimationFrame(checkAnimations);
		});
	}

	async function moveListener(event: CustomEvent<Move>) {
		if (!isPlayerMove) {
			return;
		}
		const { detail } = event;
		if (detail.lan == puzzle.moves[index].player) {
			index += 1;
			isPlayerMove = false;
			await waitForAnimations();
			chess.move(puzzle.moves[index].computer);
			isPlayerMove = true;
		} else {
			await waitForAnimations();
			chess.undo();
		}
	}
</script>

<div bind:this={boardElement}>
	<Chess
		bind:this={chess}
		on:ready={start}
		orientation={getFirstMoveColor(puzzle.fen)}
		on:move={moveListener}
	/>
</div>
