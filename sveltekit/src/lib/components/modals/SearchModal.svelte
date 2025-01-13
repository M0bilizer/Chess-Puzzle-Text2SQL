<script lang="ts">
	import { Modal, ProgressRing } from '@skeletonlabs/skeleton-svelte';
	import SearchBox from '$lib/components/SearchBox.svelte';
	import { Search } from 'lucide-svelte';
	import Fa6SolidChessKing from 'virtual:icons/fa6-solid/chess-king';
	import Fa6SolidChessQueen from 'virtual:icons/fa6-solid/chess-queen';
	import { fetchLLMData } from '$lib/utils/api';
	import HelpToolTip from '$lib/components/modals/HelpToolTip.svelte';

	let openState = $state(false);
	let query = $state('');
	let isLoading = $state(false);

	function modalClose() {
		openState = false;
	}

	const handleSQLSubmit = async (event: Event) => {
		event.preventDefault();

		try {
			isLoading = true;
			const data = await fetchLLMData(query);
			query = '';
		} catch (error) {
			toast.create({
				title: 'Error',
				description: error.message,
				type: 'error'
			});
		} finally {
			isLoading = false;
		}
	};
</script>

<Modal
	bind:open={openState}
	triggerBase="btn btn-lg preset-filled-primary-500"
	contentBase="bg-surface-50-950 rounded-container top-[10%] m-0 mx-auto max-h-[90%] w-full max-w-[90%] space-y-8 p-8 text-inherit shadow-2xl md:max-w-[75%] md:max-w-2xl lg:max-w-4xl"
	backdropBackground="bg-tertiary-500/25"
	backdropClasses="backdrop-blur-sm"
>
	{#snippet trigger()}<SearchBox />{/snippet}
	{#snippet content()}
		<div class="flex flex-1 flex-col gap-2">
			<form onsubmit={handleSQLSubmit}>
				<header class="flex items-center justify-center gap-2">
					<Fa6SolidChessKing class="size-6 text-primary-50-950" />
					<h1 class="h1">
						<span
							class="bg-gradient-radial from-tertiary-500 to-primary-500 box-decoration-clone bg-clip-text text-transparent"
							>Search for Puzzles</span
						>
					</h1>
					<Fa6SolidChessQueen class="size-6 text-primary-50-950" />
				</header>
				<div class="input-group grid-cols-[auto_1fr_auto] divide-x divide-surface-200-800">
					<div class="input-group-cell">
						<Search size={16} />
					</div>
					<input type="search" placeholder="Search..." bind:value={query} disabled={isLoading} />
					<button class="p-2 text-primary-50-950" disabled={isLoading}> Search </button>
				</div>
				<div class="type-subtitle w-full py-1 text-right">
					{#if !isLoading}
						<p>You can also search using <kbd class="kbd">Enter</kbd></p>
					{:else}
						<p>Searching...</p>
					{/if}
				</div>
				<article class="flex flex-row items-center justify-center gap-1 py-10">
					{#if !isLoading}
						<p class="text-center">This uses Text2Sql..!</p>
						<HelpToolTip />
					{:else}
						<ProgressRing
							value={null}
							size="size-14"
							meterStroke="stroke-primary-600-400"
							trackStroke="stroke-tertiary-50-950"
						/>
					{/if}
				</article>
			</form>
		</div>
	{/snippet}
</Modal>

<style>
</style>
