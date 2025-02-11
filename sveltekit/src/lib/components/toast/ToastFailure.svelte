<script lang="ts">
	import { toast } from '@zerodevx/svelte-toast';
	import Fa6SolidXmark from 'virtual:icons/fa6-solid/xmark';
	import Fa6SolidTriangleExclamation from 'virtual:icons/fa6-solid/triangle-exclamation';
	import { loadRandomPuzzle } from '$lib/utils/searchUtil.ts';
	import { closeSearchModal } from '$lib/stores/modalStore';

	let { message, toastId, query } = $props();

	function handleLoadRandomPuzzle() {
		loadRandomPuzzle(query);
		closeSearchModal();
		toast.pop(toastId);
	}

	function handleOnClick() {
		toast.pop(toastId);
	}
</script>

<div class="toast-container">
	<div
		class="card flex min-w-12 flex-row justify-around gap-6 rounded-md border-[1px] border-surface-500/20 px-4 py-2 shadow-xl bg-surface-50-950"
	>
		<div class="flex flex-col gap-1">
			<div class="flex flex-row gap-1 items-center">
				<Fa6SolidTriangleExclamation class="size-4 text-warning-500" />
				<p class="text-sm font-light text-warning-500">{message}</p>
			</div>
			<div class="flex flex-row items-center">
				<span class="text-sm text-white">Load random puzzle(s) instead?</span>
			</div>
			<div class="flex flex-row justify-end">
				<button
					class="btn btn-sm preset-filled-primary-500"
					onclick={() => handleLoadRandomPuzzle()}
				>
					<span class="text-sm text-white">OK</span>
				</button>
			</div>
		</div>
		<div class="flex items-start gap-2">
			<span class="vr"></span>
			<button onclick={() => handleOnClick()} class="mt-2">
				<Fa6SolidXmark class="size-4 text-error-500" />
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
