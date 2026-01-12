<script>
	import Typography from './components/Typography.svelte';
	import Color from './components/Color.svelte'; // Import your Color component
	import { Accordion } from '@skeletonlabs/skeleton-svelte';
	import { slide } from 'svelte/transition';

	const items = [
		{
			id: '1',
			title: 'Typography',
			component: Typography
		},
		{
			id: '2',
			title: 'Color',
			component: Color
		}
	];
</script>

<Accordion collapsible>
	{#each items as item, i (item.id)}
		{#if i !== 0}
			<hr class="hr" />
		{/if}
		<Accordion.Item value={item.id}>
			<h3>
				<Accordion.ItemTrigger class="flex items-center justify-between gap-2 font-bold">
					{item.title}
					<Accordion.ItemIndicator class="group"></Accordion.ItemIndicator>
				</Accordion.ItemTrigger>
			</h3>
			<Accordion.ItemContent>
				<div transition:slide={{ duration: 150 }}>
					<svelte:component this={item.component} />
				</div>
			</Accordion.ItemContent>
		</Accordion.Item>
	{/each}
</Accordion>
