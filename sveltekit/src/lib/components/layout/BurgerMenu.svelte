<script lang="ts">
	import { Popover, Switch } from '@skeletonlabs/skeleton-svelte';
	import Fa6SolidBars from 'virtual:icons/fa6-solid/bars';
	import { currentGame } from '$lib/stores/currentGameStore';

	let puzzleId: string = $state('');
	let openState: boolean = $state(false);
	let flipped: boolean = $state(false);

	currentGame.subscribe((state) => {
		puzzleId = state.list[state.index].puzzle.puzzleId;
	});
</script>

<Popover
	bind:open={openState}
	positioning={{ placement: 'top-end' }}
	contentBase="card border-[1px] p-4 max-w-[320px] border-surface-200-800 preset-filled-surface-100-900 z-[1000]"
>
	{#snippet trigger()}
		<Fa6SolidBars class="size-4 text-tertiary-500" />
	{/snippet}
	{#snippet content()}
		<div class="flex flex-col gap-2">
			<span class="flex items-center justify-around align-middle">
				<h6 class="h6">Flip Board</h6>
				<Switch name="example" bind:checked={flipped} />
			</span>
			<hr class="hr" />
			<a
				href="https://lichess.org/training/{puzzleId}"
				target="_blank"
				class="px-2 text-left text-sm underline hover:preset-tonal-primary"
			>
				Open puzzle in Lichess
			</a>
		</div>
	{/snippet}
</Popover>
