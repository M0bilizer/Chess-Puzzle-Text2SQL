<script lang="ts">
	import { Modal } from '@skeletonlabs/skeleton-svelte';
	import SearchBox from '$lib/components/SearchBox.svelte';
	import { Search } from 'lucide-svelte';

	let openState = $state(false);

	function modalClose() {
		openState = false;
	}
</script>

<Modal
	bind:open={openState}
	triggerBase="btn btn-lg preset-filled-primary-500"
	contentBase="bg-surface-50-950 rounded-container top-[10%] m-0 mx-auto max-h-[90%] w-full max-w-[90%] space-y-8 p-8 text-inherit shadow-xl md:max-w-[75%] md:max-w-2xl lg:max-w-4xl"
	backdropBackground="bg-tertiary-500/25"
	backdropClasses="backdrop-blur-sm"
>
	{#snippet trigger()}<SearchBox />{/snippet}
	{#snippet content()}
		<div class="flex flex-1 flex-col gap-2">
			<article>
				<div class="input-group grid-cols-[auto_1fr_auto] divide-x divide-surface-200-800">
					<div class="input-group-cell">
						<Search size={16} />
					</div>
					<input type="search" placeholder="Search..." />
				</div>
			</article>
			<header class="flex justify-between">
				<h3 class="h3">Search for Puzzles</h3>
			</header>
		</div>
	{/snippet}
</Modal>
