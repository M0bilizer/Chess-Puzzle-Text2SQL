<!-- MoveTable.svelte -->
<script lang="ts">
	import { Chess, Move } from 'chess.js';
	import type { GameState } from '../type.svelte';
	import { getPlayerColor } from './get-player-color';
	import MoveCell from './MoveCell.svelte';

	interface Props {
		gameState: GameState;
		onJumpToIndex?: (index: number) => void;
	}

	let { gameState, onJumpToIndex }: Props = $props();

	let playerColor = $derived(getPlayerColor(gameState.gameData.fen, false));
	let jumpingIndex = $derived(gameState.jumpingIndex);
	let latestIndex = $derived(gameState.latestIndex);
	let moves = $derived(gameState.gameData.moves);

	let tableCells = $derived(() => {
		const cells: Array<{ move: Move | null; index: number }> = [];

		const chess = new Chess();
		chess.load(gameState.gameData.fen);
		function toMove(lan: string | null): Move | null {
			if (!lan) return null;
			return chess.move(lan);
		}

		let actualPositionIdx = 0;

		// if player's color is white, then the first computer move is black.
		// However we don't know the corresponding white move, so we add a null cell for the starting position.
		if (playerColor === 'w') {
			cells.push({ move: null, index: -1 });
		}

		for (let i = 0; i < moves.length; i++) {
			// Add computer move if played
			if (actualPositionIdx <= latestIndex) {
				cells.push({ move: toMove(moves[i].computer), index: actualPositionIdx });
				actualPositionIdx++;
			} else {
				break;
			}

			// Add player move if played
			if (actualPositionIdx <= latestIndex) {
				cells.push({ move: toMove(moves[i].player), index: actualPositionIdx });
				actualPositionIdx++;
			} else {
				break;
			}
		}

		return cells;
	});
</script>

<div class="table-wrap rounded-t-lg">
	<table class="table w-full table-fixed">
		<colgroup>
			<col class="w-16" />
			<col class="w-1/2" />
			<col class="w-1/2" />
		</colgroup>
		<thead class="bg-surface-100-900">
			<tr class="[&>th]:text-surface-400-600">
				<th>Move</th>
				<th
					>White {#if playerColor === 'w'}(you){/if}</th
				>
				<th
					>Black {#if playerColor === 'b'}(you){/if}</th
				>
			</tr>
		</thead>
		<tbody class="[&>tr]:border-transparent!">
			{#each tableCells() as cell, index (index)}
				{#if index % 2 === 0}
					{@const whiteCell = cell}
					{@const blackCell = tableCells()[index + 1]}
					<tr>
						<th scope="row" class="border-e bg-surface-100-900">{Math.floor(index / 2) + 1}</th>
						<MoveCell
							move={whiteCell.move}
							isActive={jumpingIndex === whiteCell.index ||
								(jumpingIndex === null && latestIndex === whiteCell.index)}
							isLatest={latestIndex === whiteCell.index}
							onClick={() => onJumpToIndex?.(whiteCell.index)}
							disabled={jumpingIndex === whiteCell.index ||
								(jumpingIndex === null && latestIndex === whiteCell.index)}
						/>
						<MoveCell
							move={blackCell?.move}
							isActive={jumpingIndex === blackCell?.index ||
								(jumpingIndex === null && latestIndex === blackCell?.index)}
							isLatest={latestIndex === blackCell?.index}
							onClick={() => onJumpToIndex?.(blackCell?.index)}
							disabled={jumpingIndex === blackCell?.index ||
								(jumpingIndex === null && latestIndex === blackCell?.index)}
						/>
					</tr>
				{/if}
			{/each}

			{#if tableCells().length === 0}
				<tr>
					<th scope="row">1</th>
					<MoveCell move={undefined} disabled={true} />
					<MoveCell move={undefined} disabled={true} />
				</tr>
			{/if}
		</tbody>
	</table>
</div>
