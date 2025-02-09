<script lang="ts">
	import { toast } from '@zerodevx/svelte-toast';
	import Fa6SolidXmark from 'virtual:icons/fa6-solid/xmark';

	let { message, toastId } = $props();

	function handleOnClick() {
		toast.pop(toastId);
	}
</script>

<div class="toast-container">
	<div
		class="card flex min-h-12 min-w-12 flex-row items-center justify-around gap-2 rounded-md border-[1px] border-surface-500/20 px-4 py-2 shadow-xl bg-surface-50-950"
	>
		<p class="text-sm font-light text-primary-50">{message}</p>
		<div class="flex items-center">
			<button onclick={() => handleOnClick()}>
				<Fa6SolidXmark class="size-4 text-primary-50 " />
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
