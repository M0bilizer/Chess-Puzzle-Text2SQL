<script lang="ts">
	import type { Move } from 'chess.js';
	import { onMount } from 'svelte';
	import { Chess } from 'svelte-chess';
	import { Engine, type Game as GameType } from '../type.svelte';
	import { getPlayerColor } from '../utils';
	import type { Preferences } from '@/features/settings/preferences-state';

	interface Props {
		game: GameType;
		engine?: Engine;
		settings?: Partial<Preferences>;
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
		settings,
		onStart,
		onCorrectMove,
		onWrongMove,
		onMoveMade,
		onEnd
	}: Props = $props();

	let orientation = $derived(
		settings?.flipOrientation
			? getPlayerColor(game.fen) === 'w'
				? 'b'
				: 'w'
			: getPlayerColor(game.fen)
	);

	let boardElement: HTMLElement;
	// BIG TODO: use chessground instead of svelte-chess
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
	<Chess bind:this={chess} {orientation} on:move={moveListener} />
</div>
