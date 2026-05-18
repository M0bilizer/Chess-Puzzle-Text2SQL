<script lang="ts">
	import { Move } from 'chess.js';
	import MoveCell from './MoveCell.svelte';
	import { SvelteMap } from 'svelte/reactivity';

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

	type Row = {
		white:
			| {
					correct: {
						move: Move;
						index: number;
						feedback?: 'correct' | 'wrong';
					} | null;
					wrong: {
						move: Move;
						index: number;
						feedback?: 'correct' | 'wrong';
					}[];
			  }
			| null
			| undefined;
		black:
			| {
					correct: {
						move: Move;
						index: number;
						feedback?: 'correct' | 'wrong';
					} | null;
					wrong: {
						move: Move;
						index: number;
						feedback?: 'correct' | 'wrong';
					}[];
			  }
			| null
			| undefined;
	};
	let moveRows = $derived.by(() => {
		// 1. first, we gotta group up the wrong moves and correct move together
		const grouped: Map<
			number,
			{
				correct: {
					move: Move;
					index: number;
				} | null;
				wrong: {
					move: Move;
					index: number;
				}[];
			}
		> = new SvelteMap();
		for (const entry of movesPlayed) {
			if (!entry) continue;
			if (!grouped.has(entry.index)) {
				grouped.set(entry.index, { correct: null, wrong: [] });
			}
			const group = grouped.get(entry.index)!;
			const moveInfo = {
				move: entry.move,
				index: entry.index
			};

			if (entry.isCorrect) {
				group.correct = moveInfo;
			} else {
				group.wrong.push(moveInfo);
			}
		}
		// Sort wrong moves chronologically
		for (const group of grouped.values()) {
			group.wrong.sort((a, b) => a.index - b.index);
		}

		// 2. now we can reduce it
		let moves = [];
		moves.push({
			move: null,
			index: 0,
			attempts: []
		});
		for (const [index, group] of Array.from(grouped)) {
			const jumpIndex = index + 1;
			const isPlayerMove = jumpIndex % 2 === 0;
			if (group.correct) {
				moves.push({
					move: group.correct.move,
					index: jumpIndex,
					feedback: isPlayerMove ? 'correct' : undefined,
					attempts: group.wrong.map((w) => w.move)
				});
			} else {
				const latestWrong = group.wrong[group.wrong.length - 1];
				const attempts = group.wrong.slice(0, -1).map((w) => w.move);
				moves.push({
					move: latestWrong.move,
					index: jumpIndex,
					feedback: 'wrong',
					attempts: attempts
				});
			}
		}

		// 3. now we can build the row
		if (playerColor === 'b') {
			moves.unshift(null);
		}
		let rows = [];
		for (let i = 0; i < moves.length; i += 2) {
			rows.push({
				rowNumber: i / 2 + 1,
				white: moves[i],
				black: moves[i + 1],
				attempts: playerColor === 'w' ? moves[i]?.attempts : moves[i + 1]?.attempts
			});
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
					<th class="border-e bg-surface-100-900">{index}</th>
					<MoveCell
						move={row.white?.move}
						isActive={currentIndex === row.white?.index}
						isLatest={latestIndex === row.white?.index}
						onClick={() => row.white?.index !== undefined && onJumpToIndex?.(row.white.index)}
						feedback={row.white?.feedback as 'correct' | 'wrong'}
						disabled={row.white === null}
					/>
					<MoveCell
						move={row?.black?.move}
						isActive={currentIndex === row.black?.index}
						isLatest={latestIndex === row.black?.index}
						onClick={() => row.black?.index !== undefined && onJumpToIndex?.(row.black.index)}
						feedback={row.black?.feedback as 'correct' | 'wrong'}
						disabled={row.black === null}
					/>
				</tr>
				{#if row.attempts && row.attempts.length > 0}
					<tr class="bg-surface-100-900">
						<td colspan="3" class="text-xs">
							{row.attempts
								.map((it) => `${index}. ${playerColor === 'b' ? '...' : ''}${it.san}`)
								.join(', ')}
						</td>
					</tr>
				{/if}
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
