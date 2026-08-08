<script lang="ts">
	import { Accordion } from '@skeletonlabs/skeleton-svelte';
	import TablerChevronDown from '~icons/tabler/chevron-down';
	import TablerChevronUp from '~icons/tabler/chevron-up';

	import type { Puzzle } from '../type.svelte';
	import CopyFenButton from './CopyFenButton.svelte';
	import OpenInLichess from './OpenInLichess.svelte';
	import OpeningBadges from './OpeningBadges.svelte';
	import ShareButton from './ShareButton.svelte';
	import ThemesBadges from './ThemesBadges.svelte';

	type Props = {
		open: boolean;
		puzzle: Puzzle;
		class?: string;
	};
	let { open = $bindable(), puzzle, class: className }: Props = $props();

	let value = open ? ['1'] : undefined;
</script>

<Accordion
	collapsible
	class={className}
	{value}
	onValueChange={(value) => (open = value !== undefined)}
>
	<Accordion.Item
		value="1"
		class="preset-filled-surface-100-900 hover:preset-filled-surface-200-800 rounded-lg"
	>
		<h3>
			<Accordion.ItemTrigger class="flex items-center justify-between p-4">
				<h2 class="h2">#{puzzle.puzzleId}</h2>
				<Accordion.ItemIndicator class="group">
					<TablerChevronUp class="hidden group-data-[state=open]:block" />
					<TablerChevronDown class="block group-data-[state=open]:hidden" />
				</Accordion.ItemIndicator></Accordion.ItemTrigger
			>
		</h3>
		<Accordion.ItemContent class="mb-2 gap-4 divide-y divide-surface-200-800 px-4 pb-4">
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
				<CopyFenButton fen={puzzle.fen} class="btn-xs" />
				<OpenInLichess gameUrl={puzzle.gameUrl} class="btn-xs" />
				<ShareButton class="btn-xs" />
			</nav>
		</Accordion.ItemContent>
	</Accordion.Item>
</Accordion>
