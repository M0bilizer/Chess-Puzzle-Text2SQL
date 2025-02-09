<script>
	import { toast } from '@zerodevx/svelte-toast';
	import Fa6SolidXmark from 'virtual:icons/fa6-solid/xmark';

	let { message, toastId } = $props();

	function handleOnClick() {
		toast.pop(toastId);
	}
</script>

<div class="toast-container">
	<div
		class="card flex flex-row min-w-12 justify-around gap-2 rounded-md px-4 py-2 border-[1px] border-surface-500/20 shadow-xl bg-surface-50-950"
	>
		<div>
			<h6 class="text-sm font-medium text-primary-500">Error</h6>
			<p class="text-sm font-light text-primary-50">{message} {toastId}</p>
		</div>
		<div class="flex items-center">
			<button onclick={() => handleOnClick()}>
				<Fa6SolidXmark class="size-4 text-tertiary-500" />
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
