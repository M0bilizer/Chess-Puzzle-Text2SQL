<script lang="ts">
	import { Modal } from '@skeletonlabs/skeleton-svelte';

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
