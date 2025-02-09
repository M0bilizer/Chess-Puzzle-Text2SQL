<script lang="ts">
	import { toast } from '@zerodevx/svelte-toast';
	import Fa6SolidXmark from 'virtual:icons/fa6-solid/xmark';
	import { loadRandomPuzzle } from '$lib/utils/searchUtil.ts';
	import { searchModalState } from '$lib/stores/modalStore';

	let { message, toastId, query } = $props();

	function handleLoadRandomPuzzle() {
		loadRandomPuzzle(query);
		searchModalState.set({ open: false });
		toast.pop(toastId);
	}

	function handleOnClick() {
		toast.pop(toastId);
	}
</script>

<div class="toast-container">
	<div
		class="card flex min-w-12 flex-row justify-around gap-2 rounded-md border-[1px] border-surface-500/20 px-4 py-2 shadow-xl bg-surface-50-950"
	>
		<div class="flex flex-col gap-1">
			<div class="flex flex-col">
				<h6 class="text-sm font-medium text-primary-500">Error</h6>
				<p class="text-sm font-light text-primary-50">{message}</p>
			</div>
			<div class="flex flex-row items-center">
				<span class="text-sm text-white">Load random puzzle(s) instead?</span>
				<button
					class="btn btn-sm preset-filled-primary-500"
					onclick={() => handleLoadRandomPuzzle()}
				>
					<span class="text-sm text-white">OK</span>
				</button>
			</div>
		</div>
		<div class="flex items-center">
			<button onclick={() => handleOnClick()}>
				<Fa6SolidXmark class="size-4 text-primary-50" />
			</button>
		</div>
	</div>
</div>

<style>
	.toast-container {
		display: flex;
		width: 100%;
		justify-content: flex-end;
		align-items: flex-end;
	}
</style>
