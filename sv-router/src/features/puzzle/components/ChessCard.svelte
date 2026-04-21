<script lang="ts">
	import { Chessground } from 'svelte-chessground';
	import { p, ROUTES } from '@/router';
	import type { Puzzle } from '@/features/puzzle/type';

	type Props = {
		puzzle: Puzzle;
	};
	let { puzzle }: Props = $props();
</script>

<a
	href={p(ROUTES.PUZZLE, { params: { puzzleId: puzzle.id } })}
	class="group block max-w-64 overflow-hidden card border border-surface-200-800 preset-filled-surface-100-900 transition-all duration-200 hover:-translate-y-1 hover:shadow-lg"
>
	<!-- Chess board placeholder -->
	<header class="relative h-64 w-64">
		<Chessground fen={puzzle.fen} />

		<!-- Overlay -->
		<div
			class="absolute inset-0 z-10 flex items-center justify-center bg-black/60 opacity-0 transition-opacity duration-200 group-hover:opacity-100"
		>
			View Puzzle
		</div>
	</header>

	<!-- Content -->
	<footer class="px-2 py-1.5">
		<small class="opacity-40 transition-opacity group-hover:opacity-100">
			#{puzzle.puzzleId}
		</small>
	</footer>
</a>

<style>
	/* hide the coordinates */
	:global(.ranks) {
		visibility: hidden;
	}
	:global(.files) {
		visibility: hidden;
	}
</style>
