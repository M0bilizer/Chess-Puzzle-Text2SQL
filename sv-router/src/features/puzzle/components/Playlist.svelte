<script lang="ts">
	import { Chessground } from 'svelte5-chessground';
	import { playlistStore } from '../store/playlist.store';
	import TablerArrowBarLeft from '~icons/tabler/arrow-bar-left';
	import TablerPlay from '~icons/tabler/play';
	import TablerCheck from '~icons/tabler/check';
	import { SvelteMap } from 'svelte/reactivity';

	let listElement: HTMLUListElement | undefined;
	let itemElements = new SvelteMap<number, HTMLLIElement>();

	export function scrollToCurrent() {
		console.log('scrolling');
		if (!listElement || !$playlistStore) return;

		const currentIndex = $playlistStore.currentIndex;
		if (currentIndex === undefined) return;

		const currentItem = itemElements.get(currentIndex);
		if (!currentItem) return;

		currentItem.scrollIntoView({
			behavior: 'smooth',
			block: 'center'
		});
	}

	function setItemRef(el: HTMLLIElement, index: number) {
		if (el) {
			itemElements.set(index, el);
		}
	}
</script>

{#if $playlistStore !== null}
	<div
		class="flex h-full w-full max-w-md flex-col space-y-2 divide-y divide-surface-200-800 card preset-filled-surface-100-900 py-2"
	>
		<header class="flex shrink-0 flex-row items-center justify-between px-2 pb-1">
			<h2 class="preset-typo-subtitle">
				{$playlistStore.name}
			</h2>

			<button class="btn-icon">
				<TablerArrowBarLeft />
			</button>
		</header>
		<ul bind:this={listElement} class="flex flex-1 flex-col overflow-y-auto">
			{#each $playlistStore.puzzles as puzzle, index (puzzle.puzzleId)}
				{@const result = puzzle.result}
				<li
					use:setItemRef={index}
					class="btn flex flex-row items-center rounded-none px-0 py-1"
					class:preset-tonal-primary={result === true}
				>
					<div class="flex min-w-10 items-center justify-center">
						{#if $playlistStore.currentIndex == index}
							<TablerPlay />
						{:else if result === true}
							<TablerCheck />
						{:else}
							<span class="text-lg">{index}</span>
						{/if}
					</div>
					<div class="thumbnail mr-1 overflow-hidden rounded-sm">
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
