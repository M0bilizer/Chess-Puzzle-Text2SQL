<script lang="ts">
	import { Chessground, type Api, type Key } from 'svelte5-chessground';
	import 'svelte5-chessground/style.css';
	import { Chess, Move, SQUARES, type Square } from 'chess.js';
	import { onMount } from 'svelte';
	import { playSound } from '../utils';
	import { watch } from 'runed';
	import PromotionDialog from './PromotionDialog.svelte';
	import { SvelteMap } from 'svelte/reactivity';

	let {
		fen = $bindable(),
		orientation = 'white',
		onMove
	}: {
		fen?: string;
		orientation?: 'white' | 'black';
		onMove?: (move: Move) => void;
	} = $props();

	let cgApi: Api;
	// Internal chess instance for move validation and dests calculation
	let chess = new Chess(fen);
	// Store pending promotion data
	let showPromotion = $state(false);
	let promotionSquare = $state<string | null>(null);
	let resolvePromotion: ((piece: string | null) => void) | null = null;

	function toDests(chess: Chess): Map<Square, Square[]> {
		const dests = new SvelteMap();
		SQUARES.forEach((s) => {
			const ms = chess.moves({ square: s, verbose: true });
			if (ms.length)
				dests.set(
					s,
					ms.map((m) => m.to)
				);
		});
		return dests as Map<Square, Square[]>;
	}

	function refreshBoard() {
		if (!cgApi) return;

		const color = chess.turn() === 'w' ? 'white' : 'black';
		cgApi.set({
			fen: chess.fen(),
			orientation,
			turnColor: color,
			check: chess.inCheck() ? (chess.turn() === 'w' ? 'white' : 'black') : undefined,
			movable: {
				color: color,
				dests: toDests(chess)
			}
		});
	}

	$effect(() => {
		if (fen) {
			chess.load(fen);
			refreshBoard();
		}
	});

	watch(
		() => orientation,
		(newOrientation) => {
			if (cgApi) {
				cgApi.set({ orientation: newOrientation });
			}
		}
	);

	function isPawnPromotion(from: string, to: string): boolean {
		const piece = chess.get(from as Square);
		if (!piece || piece.type !== 'p') return false;

		const row = to[1];
		return (piece.color === 'w' && row === '8') || (piece.color === 'b' && row === '1');
	}

	async function waitForPromotion(): Promise<string | null> {
		return new Promise((resolve, reject) => {
			resolvePromotion = resolve;
		});
	}

	function handlePromotionSelect(piece: string) {
		if (resolvePromotion) {
			resolvePromotion(piece);
			resolvePromotion = null;
		}
		showPromotion = false;
		promotionSquare = null;
	}

	function handlePromotionCancel() {
		if (resolvePromotion) {
			resolvePromotion(null);
			resolvePromotion = null;
		}
		showPromotion = false;
		promotionSquare = null;
		cgApi?.cancelMove();
	}

	async function handleMove(orig: Key, dest: Key) {
		let promotion: string | undefined = undefined;
		if (isPawnPromotion(orig, dest)) {
			promotionSquare = dest;
			showPromotion = true;
			const result = await waitForPromotion();
			if (result === null) {
				cgApi.set({ fen: chess.fen() });
				refreshBoard();
				return;
			}
			promotion = result;
			cgApi.playPremove();
		}

		const move = chess.move({ from: orig, to: dest, promotion });
		if (move) {
			refreshBoard();
			playSound(!!move.captured);
			onMove?.(move);
		}
		cgApi.playPremove();
	}

	onMount(() => {
		refreshBoard();

		cgApi.set({
			movable: {
				free: false,
				dests: toDests(chess),
				events: {
					after: handleMove
				}
			}
		});
	});
</script>

<div class="relative w-full">
	<Chessground bind:api={cgApi} />

	<PromotionDialog
		bind:isOpen={showPromotion}
		{orientation}
		square={promotionSquare || undefined}
		onSelect={handlePromotionSelect}
		onCancel={handlePromotionCancel}
	/>
</div>
