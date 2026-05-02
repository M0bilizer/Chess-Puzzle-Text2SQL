<script lang="ts">
	import type { Move } from 'chess.js';
	import { onMount } from 'svelte';
	import { Chess } from 'svelte-chess';
	import { getPlayerColor } from './get-player-color';
	import { Engine, type Game as GameType } from '../type.svelte';
	import { DEFAULT_SETTINGS } from '../type.svelte';

	interface Props {
		game: GameType;
		engine?: Engine;
		settings?: Partial<typeof DEFAULT_SETTINGS>;
		onStart?: () => void;
		onCorrectMove?: (move: Move) => void;
		onWrongMove?: (move: Move) => void;
		onMoveMade?: (move: {
			move: string;
			isComputer: boolean;
			isCorrect?: boolean;
			positionIndex: number;
		}) => void;
		onEnd?: () => void;
	}

	let {
		game,
		engine: engineProp = $bindable(),
		settings: propSettings,
		onStart,
		onCorrectMove,
		onWrongMove,
		onMoveMade,
		onEnd
	}: Props = $props();

	const settings = $derived({
		...DEFAULT_SETTINGS,
		...propSettings
	});

	let boardElement: HTMLElement;
	let chess: Chess;
	let internalEngine: Engine | null = null;

	onMount(() => {
		internalEngine = new Engine(chess, game, boardElement, settings, {
			onStart,
			onCorrectMove,
			onWrongMove,
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
	<Chess
		bind:this={chess}
		orientation={getPlayerColor(game.fen, settings.flipOrientation)}
		on:move={moveListener}
	/>
</div>
