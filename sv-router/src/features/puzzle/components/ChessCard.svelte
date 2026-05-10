<script lang="ts">
	import { PATHS } from '@/router';
	import type { Puzzle } from '../type.svelte';
	import { Chessground } from 'svelte5-chessground';

	type Props = {
		puzzle: Puzzle;
	};
	let { puzzle }: Props = $props();
</script>

<a
	href={PATHS.PUZZLE(puzzle.puzzleId)}
	class="group block w-80 overflow-hidden card border border-surface-200-800 preset-filled-surface-100-900 transition-all duration-200 hover:-translate-y-1 hover:shadow-md lg:w-64"
>
	<!-- Chess board placeholder -->
	<header class="thumbnail relative h-80 w-80 lg:h-64 lg:w-64">
		<Chessground fen={puzzle.fen} />

		<!-- Overlay -->
		<div
			class="absolute inset-0 z-10 flex items-center justify-center bg-black/60 opacity-0 transition-opacity duration-200 group-hover:opacity-100"
		>
			<span class="text-primary-50"> View Puzzle </span>
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
	.thumbnail :global(.ranks),
	.thumbnail :global(.files) {
		visibility: hidden;
	}
</style>
