<script lang="ts">
	import { Accordion } from '@skeletonlabs/skeleton-svelte';
	import type { Puzzle } from '../type';

	type Props = {
		puzzle: Puzzle;
	};
	let { puzzle }: Props = $props();
</script>

<Accordion collapsible>
	<Accordion.Item value={puzzle.puzzleId}>
		<h3>
			<Accordion.ItemTrigger>#{puzzle.puzzleId}</Accordion.ItemTrigger>
		</h3>
		<Accordion.ItemContent></Accordion.ItemContent>
	</Accordion.Item>
</Accordion>
