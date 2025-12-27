<script lang="ts">
	import type { Move } from 'chess.js';
	import { onMount } from 'svelte';
	import { Chess } from 'svelte-chess';
	import type { Game } from '../types/puzzle';
	import { playSound } from '../utils/playSound';
	import { getFirstMoveColor } from '../utils/getFirstMoveColor';

	interface Props {
		puzzle: Game;
		onStart?: () => void;
		onCorrectMove?: (move: Move) => void;
		onWrongMove?: (move: Move) => void;
		onEnd?: () => void;
	}
	let { puzzle, onStart, onCorrectMove, onWrongMove, onEnd }: Props = $props();

	let boardElement: HTMLElement;
	let chess: Chess;
	let isPlayerMove = $state(false);
	let index = $state(0);
	let isProgrammaticMove = $state(false);

	onMount(() => {
		chess.load(puzzle.fen);
	});

	export async function back() {
		isProgrammaticMove = true;
		if (index == -1) return;
		if (isPlayerMove) {
			chess.undo();
			index -= 1;
			isPlayerMove = false;
		} else {
			chess.undo();
			isPlayerMove = true;
		}
		isProgrammaticMove = false;
	}

	export async function reset() {
		isProgrammaticMove = true;
		chess.load(puzzle.fen);
		isPlayerMove = false;
		index = -1;
		isProgrammaticMove = false;
	}

	export async function forward() {
		isProgrammaticMove = true;
		if (isPlayerMove) {
			chess.move(puzzle.moves[index].player);
			isPlayerMove = false;
		} else {
			if (index >= puzzle.moves.length - 1) {
				return;
			}
			index += 1;
			chess.move(puzzle.moves[index].computer);
			isPlayerMove = true;
		}
		isProgrammaticMove = false;
	}

	export async function end() {
		isProgrammaticMove = true;
		chess.load(puzzle.fen);
		puzzle.moves.forEach((it) => {
			chess.move(it.computer);
			chess.move(it.player);
		});
		index = puzzle.moves.length - 1;
		isPlayerMove = false;
		isProgrammaticMove = false;
	}

	async function start() {
		onStart?.();
		index = -1;
		await new Promise((resolve) => setTimeout(resolve, 500));
		setTimeout(() => {
			index += 1;
			chess.move(puzzle.moves[index].computer);
			isPlayerMove = true;
		}, 250);
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
		const { detail } = event;
		playSound(!!detail.captured);
		if (!isPlayerMove || isProgrammaticMove) {
			return;
		} else {
			if (detail.lan == puzzle.moves[index].player) {
				onCorrectMove?.(detail);
				index += 1;

				if (index >= puzzle.moves.length) {
					onEnd?.();
					return;
				}

				isPlayerMove = false;
				await waitForAnimations();
				setTimeout(() => {
					chess.move(puzzle.moves[index].computer);
					isPlayerMove = true;
				}, 250);
			} else {
				onWrongMove?.(detail);
				await waitForAnimations();
				setTimeout(() => {
					chess.undo();
				}, 100);
			}
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
