<script lang="ts">
	import { Modal, ProgressRing } from '@skeletonlabs/skeleton-svelte';
	import Fa6SolidChessKing from 'virtual:icons/fa6-solid/chess-king';
	import Fa6SolidChessQueen from 'virtual:icons/fa6-solid/chess-queen';
	import HelpToolTip from '$lib/components/modals/HelpToolTip.svelte';
	import { Search } from 'lucide-svelte';
	import { isLoading } from '$lib/stores/isLoading';
	import { Result, searchPuzzles } from '$lib/utils/searchUtil';
	import { searchModalState } from '$lib/stores/modalStore';
	import { SvelteToast } from '@zerodevx/svelte-toast';
	import { toastUtil } from '$lib/utils/toastUtils';

	let query = $state('');
	let open = $state(false);

	const options = {};

	searchModalState.subscribe((state) => {
		open = state.open;
	});

	async function handleSearch(event: Event) {
		event.preventDefault();
		const result: Result = await searchPuzzles(query);
		if (result === Result.Success) searchModalState.set({ open: false });
	}
</script>

<Modal
	bind:open
	contentBase="bg-surface-50-950 rounded-container top-[10%] m-0 mx-auto max-h-[90%] w-full max-w-[90%] space-y-8 p-8 text-inherit shadow-2xl md:max-w-[75%] md:max-w-2xl lg:max-w-4xl"
	backdropBackground="bg-tertiary-500/25"
	backdropClasses="backdrop-blur-sm"
>
	{#snippet content()}
		<div>
			<SvelteToast target="modal" {options} />
			<form onsubmit={handleSearch}>
				<header class="flex items-center justify-center gap-2">
					<Fa6SolidChessKing class="size-6 text-tertiary-500" />
					<h1 class="h1">
						<span
							class="bg-gradient-radial from-tertiary-500 to-primary-500 box-decoration-clone bg-clip-text text-transparent"
							>Search for Puzzles</span
						>
					</h1>
					<Fa6SolidChessQueen class="size-6 text-tertiary-500" />
				</header>
				<div class="input-group grid-cols-[auto_1fr_auto] divide-x divide-surface-200-800">
					<div class="input-group-cell">
						<Search size={16} />
					</div>
					<input type="search" placeholder="Search..." bind:value={query} disabled={$isLoading} />
					<button class="p-2 text-primary-50-950" disabled={$isLoading}> Search </button>
				</div>
				<div class="type-subtitle hidden w-full py-1 text-right sm:block">
					<p>You can also search using <kbd class="kbd">Enter</kbd></p>
				</div>
				<article class="flex items-center justify-center py-10">
					{#if !$isLoading}
						<div class="flex flex-row gap-1 p-3">
							<p class="text-center">This uses Text2Sql..!</p>
							<HelpToolTip />
						</div>
					{:else}
						<ProgressRing
							value={null}
							size="size-12"
							meterStroke="stroke-primary-600-400"
							trackStroke="stroke-tertiary-50-950"
						/>
					{/if}
				</article>
			</form>
		</div>
	{/snippet}
</Modal>
