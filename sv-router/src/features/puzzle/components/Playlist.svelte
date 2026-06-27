<script lang="ts">
	import { Chessground } from 'svelte5-chessground';
	import { currentPlaylistStore } from '../store/currentSession.store';
	import TablerArrowBarLeft from '~icons/tabler/arrow-bar-left';
	import TablerPlay from '~icons/tabler/play';
</script>

{#if $currentPlaylistStore !== null}
	<div
		class="flex h-full w-full max-w-md flex-col space-y-2 divide-y divide-surface-200-800 card preset-filled-surface-100-900 py-2"
	>
		<header class="flex shrink-0 flex-row items-center justify-between px-2 pb-1">
			<h2 class="preset-typo-subtitle">
				{$currentPlaylistStore.name}
			</h2>

			<button class="btn-icon">
				<TablerArrowBarLeft />
			</button>
		</header>
		<ul class="flex flex-1 flex-col gap-1 overflow-y-auto px-2">
			{#each $currentPlaylistStore.puzzles as puzzle, index (puzzle.puzzleId)}
				<li class="flex flex-row items-center">
					<div class="min-w-10">
						{#if $currentPlaylistStore.currentIndex == index}
							<TablerPlay />
						{:else}{/if}
					</div>
					<div class="thumbnail">
						<Chessground fen={puzzle.fen} orientation={puzzle.orientation} />
					</div>
				</li>
			{/each}
		</ul>
	</div>
{/if}

<style>
	/* hide the coordinates */
	.thumbnail :global(.ranks),
	.thumbnail :global(.files) {
		visibility: hidden;
	}
</style>
