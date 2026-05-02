<!-- MoveTable.svelte -->
<script lang="ts">
	import { Chess, Move } from 'chess.js';
	import type { GameState } from '../type.svelte';
	import { getPlayerColor } from './get-player-color';
	import MoveCell from './MoveCell.svelte';

	interface Props {
		gameState: GameState;
		wrongAttempts: Map<number, string>;
	}

	let { gameState, wrongAttempts }: Props = $props();

	let currentPositionIndex = $derived(gameState.positionIndex);
	let moves = $derived(gameState.gameData.moves);

	// TODO: move this out, moveTable should be only presentation
	let tableCells = $derived(() => {
		const cells: Array<Move | null> = [];
		const color = getPlayerColor(gameState.gameData.fen, false);

		// Lichess recommends using a chess library to convert LAN to SAN
		const chess = new Chess();
		chess.load(gameState.gameData.fen);
		function toMove(lan: string | null): Move | null {
			if (!lan) return null;
			return chess.move(lan);
		}

		if (color === 'w') {
			cells.push(null);
		}
		for (const move of moves) {
			cells.push(toMove(move.computer));
			cells.push(toMove(move.player));
		}
		if (color === 'w') {
			cells.push(null);
		}
		return cells;
	});
</script>

<table class="table">
	<thead>
		<tr>
			<th>Move</th>
			<th>White</th>
			<th>Black</th>
		</tr>
	</thead>
	<tbody>
		{#each tableCells() as cell, index (index)}
			{#if index % 2 === 0}
				<tr class="[&>td]:hover:preset-filled-primary-50-950">
					<th scope="row">{Math.floor(index / 2) + 1}</th>
					<MoveCell move={cell} />
					<MoveCell move={tableCells()[index + 1]} />
				</tr>
			{/if}
		{/each}
	</tbody>
</table>
