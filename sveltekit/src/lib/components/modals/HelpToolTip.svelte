<script lang="ts">
	import { Tooltip } from '@skeletonlabs/skeleton-svelte';
	import { CircleHelp } from 'lucide-svelte';
	let openState = $state(false);
</script>

<Tooltip
	bind:open={openState}
	positioning={{ placement: 'top' }}
	triggerBase="cursor-default"
	contentBase="card preset-filled-surface-100-900 shadow-xl p-4"
	openDelay={200}
	classes="hidden sm:block"
>
	{#snippet trigger()}<CircleHelp class="align-vertical size-4" />{/snippet}
	{#snippet content()}Text2Sql allows you to search using natural language!{/snippet}
</Tooltip>
