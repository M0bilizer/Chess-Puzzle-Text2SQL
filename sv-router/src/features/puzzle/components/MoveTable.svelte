<script lang="ts">
	import { Move } from 'chess.js';
	import MoveCell from './MoveCell.svelte';

	let {
		movesPlayed,
		playerColor,
		currentIndex = $bindable(),
		latestIndex = $bindable(),
		onJumpToIndex,
		class: className
	}: {
		movesPlayed: (
			| {
					index: number;
					move: Move;
					isComputer: boolean;
					isCorrect: boolean;
			  }
			| undefined
		)[];
		playerColor: 'w' | 'b';
		currentIndex: number;
		latestIndex: number;
		onJumpToIndex?: (index: number) => void;
		class?: string;
	} = $props();

	let moveRows = $derived.by(() => {
		const rows: Array<{
			rowNumber: number;
			white: {
				move: Move | null | undefined;
				index: number;
				feedback?: 'correct' | 'wrong';
			} | null;
			black: {
				move: Move | null | undefined;
				index: number;
				feedback?: 'correct' | 'wrong';
			} | null;
		}> = [];

		let rowCounter = 1;
		let currentRow: {
			rowNumber: number;
			white: {
				move: Move | null | undefined;
				index: number;
				feedback?: 'correct' | 'wrong';
			} | null;
			black: {
				move: Move | null | undefined;
				index: number;
				feedback?: 'correct' | 'wrong';
			} | null;
		} = {
			rowNumber: rowCounter++,
			white: null,
			black: null
		};

		for (let i = 0; i < movesPlayed.length; i++) {
			const entry = movesPlayed[i];
			// if entry was undefined then it must be initial state
			if (!entry) {
				// it must be the player's color
				const isWhite = playerColor === 'w';
				if (isWhite) {
					currentRow = {
						...currentRow,
						white: { move: null, index: 0 }
					};
				} else {
					currentRow = {
						...currentRow,
						white: { move: undefined, index: -1 },
						black: { move: null, index: 0 }
					};
				}
			} else {
				if (!entry.isCorrect) continue;
				const feedback = entry.isComputer ? undefined : entry.isCorrect ? 'correct' : 'wrong';
				const isWhite = entry.move.color === 'w';
				if (isWhite) {
					currentRow = {
						...currentRow,
						white: {
							move: entry.move,
							index: entry.index + 1,
							feedback: feedback
						}
					};
				} else {
					currentRow = {
						...currentRow,
						black: {
							move: entry.move,
							index: entry.index + 1,
							feedback: feedback
						}
					};
				}
			}
			if (currentRow.black != null && currentRow.white !== null) {
				rows.push(currentRow);
				currentRow = {
					rowNumber: rowCounter++,
					white: null,
					black: null
				};
			}
		}

		// Push incomplete row if exists (last move without response)
		if (currentRow.white || currentRow.black) {
			rows.push(currentRow);
		}
		return rows;
	});
</script>

<div class="table-wrap rounded-t-lg {className}">
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
			{#each moveRows as row, index (index)}
				<tr>
					<th class="border-e bg-surface-100-900">{row.rowNumber}</th>
					<MoveCell
						move={row.white?.move}
						isActive={currentIndex === row.white?.index}
						isLatest={latestIndex === row.white?.index}
						onClick={() => row.white && onJumpToIndex?.(row.white.index)}
						feedback={row.white?.feedback}
						disabled={row.white?.index === -1}
					/>
					<MoveCell
						move={row.black?.move}
						isActive={currentIndex === row.black?.index}
						isLatest={latestIndex === row.black?.index}
						onClick={() => row.black && onJumpToIndex?.(row.black.index)}
						feedback={row.black?.feedback}
						disabled={row.black?.index === -1}
					/>
				</tr>
			{/each}

			{#if moveRows.length === 0}
				<tr>
					<th class="border-e bg-surface-100-900">1</th>
					<MoveCell move={null} disabled={true} />
					<MoveCell move={null} disabled={true} />
				</tr>
			{/if}
		</tbody>
	</table>
</div>
