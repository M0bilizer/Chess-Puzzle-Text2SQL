<script lang="ts">
	import type { Move } from 'chess.js';
	import { onMount } from 'svelte';
	import { Chess } from 'svelte-chess';
	import { Engine, type Game as GameType } from '../type.svelte';
	import { getPlayerColor, playSound } from '../utils';
	import type { Preferences } from '@/features/settings/preferences-state';
	import { watch } from 'runed';

	interface Props {
		game: GameType;
		engine?: Engine;
		settings: Preferences;
		onStart?: () => void;
		onEnd?: () => void;
	}

	let {
		game,
		engine: engineProp = $bindable(),
		settings = $bindable(),
		onStart,
		onEnd
	}: Props = $props();

	let orientation = $derived(getPlayerColor(game.fen));
	watch(
		() => settings.flipOrientation,
		(curr, prev) => {
			if (curr !== prev) {
				chess.toggleOrientation();
			}
		},
		{ lazy: true }
	);

	let boardElement: HTMLElement;
	// BIG TODO: use chessground instead of svelte-chess
	let chess: Chess;
	let internalEngine: Engine | null = null;

	function onMoveMade(move: {
		raw: Move;
		isComputer: boolean;
		isCorrect?: boolean;
		positionIndex: number;
	}) {
		if (!settings.muted) {
			playSound(!!move.raw.captured);
		}
	}

	onMount(() => {
		internalEngine = new Engine(chess, game, boardElement, settings, {
			onStart,
			onMoveMade,
			onEnd
		});
		engineProp = internalEngine;
		internalEngine.actions.start();
	});

	async function moveListener(event: CustomEvent<Move>) {
		const { detail } = event;
		await internalEngine?.handleMove(detail);
	}
</script>

<div bind:this={boardElement}>
	<Chess bind:this={chess} {orientation} on:move={moveListener} />
</div>
