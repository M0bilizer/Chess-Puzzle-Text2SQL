<script lang="ts">
	import { Modal, ProgressRing } from '@skeletonlabs/skeleton-svelte';
	import SearchBox from '$lib/components/SearchBox.svelte';
	import { Toaster } from 'svelte-sonner';
	import Fa6SolidChessKing from 'virtual:icons/fa6-solid/chess-king';
	import Fa6SolidChessQueen from 'virtual:icons/fa6-solid/chess-queen';
	import HelpToolTip from '$lib/components/modals/HelpToolTip.svelte';
	import { Search } from 'lucide-svelte';
	import { isLoading } from '$lib/stores/isLoading';
	import { Result, searchPuzzles } from '$lib/utils/searchUtil';
	import { goto } from '$app/navigation';

	let modalOpen = $state(false);
</script>

<Modal
	bind:open={modalOpen}
	contentBase="bg-surface-50-950 rounded-container top-[10%] m-0 mx-auto max-h-[90%] w-full max-w-[90%] space-y-8 p-8 text-inherit shadow-2xl md:max-w-[75%] md:max-w-2xl lg:max-w-4xl"
	backdropBackground="bg-tertiary-500/25"
	backdropClasses="backdrop-blur-sm"
>
	{#snippet trigger()}<span class="text-sm text-primary-500 hover:text-primary-700 hover:underline"
			>Debug</span
		>
	{/snippet}
	{#snippet content()}
		<div>ok</div>
	{/snippet}
</Modal>
