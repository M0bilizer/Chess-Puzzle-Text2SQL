<script lang="ts">
	import TablerCheck from '~icons/tabler/check';
	import TablerX from '~icons/tabler/x';
	import TablerChessKing from '~icons/tabler/chess-king';
	import TablerChessKingFilled from '~icons/tabler/chess-king-filled';
	import TablerTrophy from '~icons/tabler/trophy';
	import TablerPlay from '~icons/tabler/play';
	type Props = {
		playerColor: 'w' | 'b';
		isComplete: boolean;
		moveResult?: 'correct' | 'wrong';
		onHint: () => void;
		onSolution: () => void;
		hasNext: boolean;
		onNext: () => void;
	};
	let {
		playerColor,
		isComplete,
		moveResult = undefined,
		onHint,
		onSolution,
		hasNext,
		onNext
	}: Props = $props();
</script>

<section
	class="flex h-[140px] w-full flex-col items-center justify-center gap-4 rounded-b-lg preset-filled-surface-100-900"
>
	{#if isComplete}
		{#if hasNext}
			<button onclick={onNext} class="btn h-[100px] w-full rounded-none preset-tonal-primary">
				<TablerPlay class="size-12" /><span class="text-2xl">Next Puzzle </span>
			</button>
		{:else}
			<div class="feedback">
				<TablerTrophy class="size-16 text-success-950-50" />
				<div class="instruction">
					<strong class="text-2xl">Success</strong>
					<em class="mt-1 text-sm">Puzzle completed</em>
				</div>
			</div>
		{/if}
	{:else if moveResult === 'wrong'}
		<div class="feedback">
			<TablerX class="size-16 text-error-950-50" />
			<div class="instruction">
				<strong class="text-xl">That's not the move</strong>
				<em class="text-sm">Try something else.</em>
			</div>
		</div>
	{:else if moveResult === 'correct'}
		<div class="feedback">
			<TablerCheck class="size-16 text-primary-950-50" />
			<div class="instruction">
				<strong class="text-xl">Best move!</strong>
				<em class="text-sm">Keep going...</em>
			</div>
		</div>
	{:else}
		<div class="feedback">
			<span class="text-surface-950 dark:text-surface-50">
				{#if playerColor === 'w'}
					<span class="block dark:hidden"><TablerChessKing class="size-16" /></span>
					<span class="hidden dark:block"><TablerChessKingFilled class="size-16" /></span>
				{:else}
					<span class="hidden dark:block"><TablerChessKing class="size-16" /></span>
					<span class="block dark:hidden"><TablerChessKingFilled class="size-16" /></span>
				{/if}
			</span>
			<div class="instruction">
				<strong class="text-xl">Your turn</strong>
				<em class="text-sm">It's your turn!</em>
			</div>
		</div>
	{/if}
	{#if !isComplete}
		<div class="flex w-full justify-around">
			<button class="btn-small btn preset-filled-surface-100-900" onclick={onHint}> Hint </button>
			<button class="btn-small btn preset-filled-surface-100-900" onclick={onSolution}>
				Solution
			</button>
		</div>
	{/if}
</section>

<style>
	.feedback {
		display: flex;
		align-items: center;
		gap: 1rem;
	}

	.instruction {
		display: flex;
		flex-direction: column;
	}
</style>
