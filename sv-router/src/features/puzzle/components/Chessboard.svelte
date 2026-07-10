<script lang="ts">
	import { Chessground, type Api, type Key } from 'svelte5-chessground';
	import 'svelte5-chessground/style.css';
	import { Chess, Move, SQUARES, type Square } from 'chess.js';
	import { onMount } from 'svelte';
	import { playSound } from '../utils';
	import { watch } from 'runed';
	import PromotionDialog from './PromotionDialog.svelte';
	import { SvelteMap } from 'svelte/reactivity';
	import { type Preferences } from '@/features/settings/preferences-state';

	type Props = {
		fen: string;
		settings: Preferences;
		orientation?: 'white' | 'black';
		onMove?: (move: Move) => Promise<void>;
		interactive?: boolean;
	};

	let {
		fen = $bindable(),
		settings = $bindable(),
		orientation = 'white',
		onMove,
		interactive = $bindable(true),
	}: Props = $props();

	let cgApi: Api | undefined = $state();
	// Internal chess instance for move validation and dests calculation
	let chess = new Chess(fen);
	let showPromotion = $state(false);
	let promotionSquare = $state<string | null>(null);
	let resolvePromotion: ((piece: string | null) => void) | null = null;
	let containerElement = $state<HTMLElement | null>(null);

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
			},
			animation: {
				enabled: true,
				duration: settings.animationSpeed
			},
		});
	}

	// When parent update fen, update the chess instance and refresh the board
	watch(
		() => fen,
		(newFen) => {
			if (chess.fen() !== newFen) {
				chess.load(newFen);
				refreshBoard();
			}
		}
	);

	// This sync the chessboard's props with the cgApi
	watch(
		[() => orientation, () => interactive],
		([newOrientation, newInteractive]) => {
			if (cgApi) {
				cgApi.set({ orientation: newOrientation, viewOnly: !newInteractive });
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
		return new Promise((resolve) => {
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
				cgApi?.set({ fen: chess.fen() });
				refreshBoard();
				return;
			}
			promotion = result;
			cgApi?.playPremove();
		}

		const move = chess.move({ from: orig, to: dest, promotion });
		if (move) {
			fen = chess.fen();
			refreshBoard();
			if (!settings.muted) playSound(!!move.captured);
			await onMove?.(move);
		}
		cgApi?.playPremove();
	}

	onMount(() => {
		refreshBoard();
		cgApi?.set({
			movable: {
				free: false,
				dests: toDests(chess),
				events: {
					after: handleMove
				}
			},
		});
	});

	export function getElement(): HTMLElement | null {
		return containerElement;
	}

	export function makeMove(from: string, to: string, promotion?: string) {
		const move = chess.move({ from, to, promotion });
		if (!move) return false;

		// Update UI with animation
		fen = chess.fen();
		refreshBoard();
		if (!settings.muted) playSound(!!move.captured);
	}

	export async function undo() {
		const previousMove = chess.undo();
		if (!previousMove) return false;

		fen = chess.fen();
		if (!settings.muted) playSound(false);
		refreshBoard();
	}

	export function waitForAnimations(): Promise<void> {
		return new Promise<void>((resolve) => {
			if (!containerElement) {
				return Promise.resolve();
			}
			const hasAnimation = containerElement.querySelector('.anim');
			if (!hasAnimation) {
				resolve();
				return;
			}
			const observer = new MutationObserver(() => {
				const currentAnimations = containerElement?.querySelector('.anim');
				if (!currentAnimations) {
					observer.disconnect();
					resolve();
				}
			});
			observer.observe(containerElement, {
				childList: true,
				subtree: true,
				attributes: true,
				attributeFilter: ['class']
			});
		});
	}

	export function selectSquare(key: Key | null) {
		cgApi?.selectSquare(key);
	}
</script>

<div bind:this={containerElement} class="relative">
	<Chessground bind:api={cgApi} />

	<PromotionDialog
		bind:isOpen={showPromotion}
		{orientation}
		square={promotionSquare || undefined}
		onSelect={handlePromotionSelect}
		onCancel={handlePromotionCancel}
	/>
</div>
