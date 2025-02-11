<script lang="ts">
	import { toast } from '@zerodevx/svelte-toast';
	import Fa6SolidXmark from 'virtual:icons/fa6-solid/xmark';
	import { searchPuzzles } from '$lib/utils/searchUtil';
	import { ModelEnum } from '$lib/enums/modelEnum';
	import { SearchResultEnum } from '$lib/enums/searchResultEnum';
	import { searchModalState } from '$lib/stores/modalStore';
	import Fa6SolidTriangleExclamation from 'virtual:icons/fa6-solid/triangle-exclamation';

	let { message, toastId, query } = $props();

	async function handleSearchPuzzle() {
		const result: SearchResultEnum = await searchPuzzles(query, ModelEnum.Mistral);
		if (result === SearchResultEnum.Success) searchModalState.set({ open: false });
	}

	function handleOnClick() {
		toast.pop(toastId);
	}
</script>

<div class="toast-container">
	<div
		class="card flex min-w-12 flex-row justify-around gap-6 rounded-md border-[1px] border-surface-500/20 px-4 py-2 shadow-xl bg-error-50-950"
	>
		<div class="flex flex-col gap-1">
			<div class="flex flex-row gap-1 items-center">
				<Fa6SolidTriangleExclamation class="size-4 text-error-500" />
				<p class="text-sm font-light text-error-500">{message}</p>
			</div>
			<div class="flex flex-row items-center">
				<span class="text-sm text-white">Use alternative engine instead?</span>
			</div>
			<div class="flex flex-row justify-end">
				<button
					class="btn btn-sm preset-filled-error-500"
					onclick={() => handleSearchPuzzle()}
				>
					<span class="text-sm text-white">Search</span>
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
