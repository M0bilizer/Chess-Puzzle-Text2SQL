<script lang="ts">
	import { Accordion } from '@skeletonlabs/skeleton-svelte';
	import TablerChevronDown from '~icons/tabler/chevron-down';
	import TablerChevronUp from '~icons/tabler/chevron-up';
	import CopyFenButton from './CopyFenButton.svelte';
	import OpenInLichess from './OpenInLichess.svelte';
	import OpeningBadges from './OpeningBadges.svelte';
	import ThemesBadges from './ThemesBadges.svelte';
	import ShareButton from './ShareButton.svelte';
	import type { Puzzle } from '../type.svelte';

	type Props = {
		puzzle: Puzzle;
		class?: string;
	};
	let { puzzle, class: className }: Props = $props();
</script>

<Accordion collapsible class={className}>
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
			class="mb-2 gap-4 divide-y divide-surface-200-800 preset-filled-surface-100-900 p-4"
		>
			<section class="space-x-2">
				<small class="opacity-50">
					Rating: {puzzle.rating}
				</small>
				<small class="opacity-50">
					Played {puzzle.nbPlays} times
				</small>
			</section>
			<dl class="flex flex-col gap-1 p-2">
				<dt class="font-semibold">Openings</dt>
				<OpeningBadges openings={puzzle.openingTags} />
			</dl>
			<dl class="flex flex-col gap-1 p-2">
				<dt class="font-semibold">Themes</dt>
				<ThemesBadges themes={puzzle.themes} />
			</dl>
			<nav class="flex items-center gap-4 p-2">
				<CopyFenButton fen={puzzle.fen} />
				<OpenInLichess gameUrl={puzzle.gameUrl} />
				<ShareButton />
			</nav>
		</Accordion.ItemContent>
	</Accordion.Item>
</Accordion>
