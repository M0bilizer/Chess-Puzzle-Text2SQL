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

	let jumpingIndex = $derived(gameState.jumpingIndex);
	let latestIndex = $derived(gameState.latestIndex);
	let moves = $derived(gameState.gameData.moves);

	let tableCells = $derived(() => {
		const cells: Array<Move | null> = [];
		const color = getPlayerColor(gameState.gameData.fen, false);

		// Lichess recommends using a chess library to convert IAN to SAN
		const chess = new Chess();
		chess.load(gameState.gameData.fen);
		function toMove(lan: string | null): Move | null {
			if (!lan) return null;
			return chess.move(lan);
		}

		let positionIdx = 0;

		// Add initial empty cell if needed (always visible since it's the starting position)
		if (color === 'w') {
			cells.push(null);
		}

		for (let i = 0; i < moves.length; i++) {
			// Add computer move if played
			if (positionIdx <= latestIndex) {
				cells.push(toMove(moves[i].computer));
				positionIdx++;
			} else {
				break; // Stop adding once we reach unplayed moves
			}

			// Add player move if played
			if (positionIdx <= latestIndex) {
				cells.push(toMove(moves[i].player));
				positionIdx++;
			} else {
				break; // Stop adding once we reach unplayed moves
			}
		}

		return cells;
	});

	$inspect(latestIndex, jumpingIndex);
</script>

<table class="table w-full table-fixed">
	<colgroup>
		<col class="w-16" />
		<col class="w-1/2" />
		<col class="w-1/2" />
	</colgroup>
	<thead>
		<tr>
			<th>Move</th>
			<th>White</th>
			<th>Black</th>
		</tr>
	</thead>
	<tbody class="[&>tr]:border-transparent!">
		{#each tableCells() as cell, index (index)}
			{#if index % 2 === 0}
				<tr class="[&>td]:hover:preset-filled-primary-50-950">
					<th scope="row" class="border-e bg-surface-100-900">{Math.floor(index / 2) + 1}</th>
					<MoveCell
						move={cell}
						isActive={jumpingIndex === index || (jumpingIndex === null && latestIndex === index)}
						isLatest={latestIndex === index}
					/>
					<MoveCell
						move={tableCells()[index + 1]}
						isActive={jumpingIndex === index + 1 ||
							(jumpingIndex === null && latestIndex === index + 1)}
						isLatest={latestIndex === index + 1}
					/>
				</tr>
			{/if}
		{/each}

		{#if tableCells().length === 0}
			<tr class="[&>td]:hover:preset-filled-primary-50-950">
				<th scope="row">1</th>
				<MoveCell move={undefined} />
				<MoveCell move={undefined} />
			</tr>
		{/if}
	</tbody>
</table>
