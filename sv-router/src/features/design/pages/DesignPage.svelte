<script>
	import Typography from '../components/Typography.svelte';
	import { Accordion } from '@skeletonlabs/skeleton-svelte';
	import { slide } from 'svelte/transition';
	import { quintOut } from 'svelte/easing';
	import Iconography from '../components/Iconography.svelte';

	const items = [
		{
			id: '1',
			title: 'Typography',
			component: Typography
		},
		{
			id: '2',
			title: 'Icons',
			component: Iconography
		}
	];
</script>

<Accordion collapsible>
	{#each items as item, i (item)}
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
				{#snippet element(attributes)}
					{#if !attributes.hidden}
						<div {...attributes} transition:slide={{ duration: 150, easing: quintOut }}>
							<svelte:component this={item.component} />
						</div>
					{/if}
				{/snippet}
			</Accordion.ItemContent>
		</Accordion.Item>
	{/each}
</Accordion>
