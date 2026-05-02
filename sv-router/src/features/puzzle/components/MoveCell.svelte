<script lang="ts">
	import TablerChess from '~icons/tabler/chess';
	import TablerChessFilled from '~icons/tabler/chess-filled';
	import TablerChessBishop from '~icons/tabler/chess-bishop';
	import TablerChessBishopFilled from '~icons/tabler/chess-bishop-filled';
	import TablerChessKing from '~icons/tabler/chess-king';
	import TablerChessKingFilled from '~icons/tabler/chess-king-filled';
	import TablerChessKnight from '~icons/tabler/chess-knight';
	import TablerChessKnightFilled from '~icons/tabler/chess-knight-filled';
	import TablerChessQueen from '~icons/tabler/chess-queen';
	import TablerChessQueenFilled from '~icons/tabler/chess-queen-filled';
	import TablerChessRook from '~icons/tabler/chess-rook';
	import TablerChessRookFilled from '~icons/tabler/chess-rook-filled';
	import type { Move } from 'chess.js';

	type Props = {
		move: Move | null | undefined;
	};
	const { move }: Props = $props();

	const pieceIcons = {
		p: { outline: TablerChess, filled: TablerChessFilled },
		b: { outline: TablerChessBishop, filled: TablerChessBishopFilled },
		n: { outline: TablerChessKnight, filled: TablerChessKnightFilled },
		r: { outline: TablerChessRook, filled: TablerChessRookFilled },
		q: { outline: TablerChessQueen, filled: TablerChessQueenFilled },
		k: { outline: TablerChessKing, filled: TablerChessKingFilled }
	};
</script>

<td>
	<div class="flex items-center gap-1">
		{#if move}
			{@const Icon = pieceIcons[move.piece as keyof typeof pieceIcons]}
			{@const isWhite = move.color === 'w'}

			<span class="text-surface-950 dark:text-surface-50">
				{#if isWhite}
					<!-- White: outline in light mode, filled in dark mode -->
					<span class="block dark:hidden"><Icon.outline /></span>
					<span class="hidden dark:block"><Icon.filled /></span>
				{:else}
					<!-- Black: filled in light mode, outline in dark mode -->
					<span class="hidden dark:block"><Icon.outline /></span>
					<span class="block dark:hidden"><Icon.filled /></span>
				{/if}
			</span>
			<span>
				{move.san}
			</span>
		{:else if move === null}
			<span class="text-surface-500">—</span>
		{:else}
			<!-- undefined -->
			<span></span>
		{/if}
	</div>
</td>
