<script lang="ts">
	import { Accordion } from '@skeletonlabs/skeleton-svelte';
	import TablerChevronDown from '~icons/tabler/chevron-down';
	import TablerChevronUp from '~icons/tabler/chevron-up';
	import type { Puzzle } from '../type';
	import CopyFenButton from './CopyFenButton.svelte';

	type Props = {
		puzzle: Puzzle;
	};
	let { puzzle }: Props = $props();
</script>

<Accordion collapsible>
	<Accordion.Item value={puzzle.puzzleId}>
		<h3>
			<Accordion.ItemTrigger
				class="flex items-center justify-between preset-filled-surface-100-900 p-4 hover:preset-filled-surface-200-800"
			>
				<h2 class="h2">#{puzzle.puzzleId}</h2>
				<Accordion.ItemIndicator class="group">
					<TablerChevronUp class="size-icon-big hidden group-data-[state=open]:block" />
					<TablerChevronDown class="size-icon-big block group-data-[state=open]:hidden" />
				</Accordion.ItemIndicator></Accordion.ItemTrigger
			>
		</h3>
		<Accordion.ItemContent
			class="gap-4 divide-y divide-surface-200-800 preset-filled-surface-100-900 p-4"
		>
			<article class="space-y-4 p-4">
				<div>
					<h2 class="h6">Announcements</h2>
					<h3 class="h3">Skeleton is Awesome</h3>
				</div>
				<p class="opacity-60">
					Lorem ipsum dolor sit amet consectetur adipisicing elit. Numquam aspernatur provident
					eveniet eligendi cumque consequatur tempore sint nisi sapiente. Iste beatae laboriosam
					iure molestias cum expedita architecto itaque quae rem.
				</p>
			</article>
			<!--  -->
			<footer class="flex items-center justify-between gap-4 p-4">
				<CopyFenButton fen={puzzle.fen} />
				<small class="opacity-60">By Alex</small>
				<small class="opacity-60">On {new Date().toLocaleDateString()}</small>
			</footer>
		</Accordion.ItemContent>
	</Accordion.Item>
</Accordion>
