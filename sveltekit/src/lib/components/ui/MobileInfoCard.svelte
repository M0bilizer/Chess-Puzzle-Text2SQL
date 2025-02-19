<script lang="ts">
	import { Segment } from '@skeletonlabs/skeleton-svelte';
	import RecentSearches from '$lib/components/ui/RecentSearches.svelte';
	import QueryDisplay from '$lib/components/ui/QueryDisplay.svelte';
	import PuzzleDescription from '$lib/components/ui/PuzzleDescription.svelte';
	import { currentGame } from '$lib/stores/currentGameStore';

	let page = $state('search');

	let openings: string[] = $state([]);
	let themes: string[] = $state([]);
	currentGame.subscribe((state) => {
		if (state.list.length) {
			const puzzle = state.list[state.index].puzzle;
			openings = puzzle.openingTags.split(' ');
			themes = puzzle.themes.split(' ');
		}
	});
</script>

<div class="flex flex-col gap-2 p-2">
	<div class="flex flex-col items-center justify-center">
		<Segment
			name="align"
			bind:value={page}
			base="inline-flex items-stretch overflow-hidden flex-row gap-2 justify-center"
			background=""
			indicatorBg="top-[var(--top)] left-[var(--left)] w-[var(--width)] h-[var(--height)] preset-filled-primary-500 rounded"
			indicatorText="preset-primary-50-950"
		>
			<Segment.Item value="search" labelBase="text-sm">Search History</Segment.Item>
			<Segment.Item value="puzzle" labelBase="text-sm">Puzzle Info</Segment.Item>
		</Segment>
		<hr class="hr" />
	</div>
	<div class="grid grid-cols-1 gap-y-2">
		{#if page === 'search'}
			<QueryDisplay />
			<RecentSearches />
		{:else}
			<PuzzleDescription />
			<div class="flex flex-col gap-2">
				<div
					class="card h-max w-full max-w-md gap-2 border-[1px] px-4 pb-2 pt-4 text-center shadow-xl border-surface-200-800 preset-filled-surface-100-900"
				>
					<h4 class="h4">Opening Tags</h4>
					<hr class="hr" />
					<div class="overflow-auto">
						{#each openings as row}
							<div class="truncate px-2 text-left text-sm">
								{row}
							</div>
						{/each}
					</div>
				</div>
				<div
					class="card h-max w-full max-w-md gap-2 border-[1px] px-4 pb-2 pt-4 text-center shadow-xl border-surface-200-800 preset-filled-surface-100-900"
				>
					<h4 class="h4">Themes</h4>
					<hr class="hr" />
					<div class="overflow-auto">
						{#each themes as row}
							<div class="truncate px-2 text-left text-sm">
								{row}
							</div>
						{/each}
					</div>
				</div>
			</div>
		{/if}
	</div>
</div>
