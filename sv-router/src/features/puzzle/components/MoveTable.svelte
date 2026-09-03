<script lang="ts">
	import { Move } from 'chess.js';
	import { SvelteMap } from 'svelte/reactivity';

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
				black: moves[i + 1] ?? null,
				attempts: playerColor === 'w' ? moves[i]?.attempts : moves[i + 1]?.attempts
			});
		}
		return rows;
	});
</script>

<section class={[className, 'divide-y-1 divide-surface-200-800']}>
	<header class="move-row *:text-sm *:bg-surface-100-900 *:text-surface-400-600">
		<div class="rounded-tl-lg">Move</div>
		<div>
			White {#if playerColor === 'w'}(you){/if}
		</div>
		<div class="rounded-tr-lg">
			Black {#if playerColor === 'b'}(you){/if}
		</div>
	</header>
	<ol class="h-full">
		{#each moveRows as row, index (index)}
			<li class="move-row">
				<span class="border-e bg-surface-100-900">{index}</span>
				<MoveCell
					move={row.white?.move}
					isActive={currentIndex === row.white?.index}
					isLatest={latestIndex === row.white?.index}
					onClick={() =>
						row.white?.index !== undefined &&
						row.white?.feedback !== 'wrong' &&
						onJumpToIndex?.(row.white.index)}
					feedback={row.white?.feedback as 'correct' | 'wrong'}
					disabled={row.white === null}
				/>
				<MoveCell
					move={row?.black?.move}
					isActive={currentIndex === row.black?.index}
					isLatest={latestIndex === row.black?.index}
					onClick={() =>
						row.black?.index !== undefined &&
						row.black?.feedback !== 'wrong' &&
						onJumpToIndex?.(row.black.index)}
					feedback={row.black?.feedback as 'correct' | 'wrong'}
					disabled={row.black === null}
				/>
			</li>
		{/each}

		{#if moveRows.length === 0}
			<li class="move-row">
				<span class="border-e bg-surface-100-900">1</span>
				<MoveCell move={null} disabled={true} />
				<MoveCell move={null} disabled={true} />
			</li>
		{/if}
	</ol>
</section>

<style lang="scss">
	@reference "@/app.css";
	.move-row {
		@apply grid grid-cols-[64px_132px_132px] content-start *:p-2;
	}
</style>
