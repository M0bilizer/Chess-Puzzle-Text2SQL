<script lang="ts">
	import { Modal } from '@skeletonlabs/skeleton-svelte';
	import { currentGame } from '$lib/stores/currentGameStore';
	import { congratulationModalState } from '$lib/stores/modalStore';

	let query: string = '';
	let length: number = 0;

	currentGame.subscribe((state) => {
		query = state.query;
		length = state.list.length;
	});

	let open = false;
	congratulationModalState.subscribe((state) => {
		open = state.open;
	});

	function closeModal() {
		congratulationModalState.set({ open: false });
	}
</script>

<Modal
	bind:open
	contentBase="md:max-w-sm md:max-w-sm lg:max-w-sm bg-surface-50-950 rounded-container top-[10%] m-0 mx-auto max-h-[90%] w-full max-w-[90%] space-y-8 p-8 text-inherit shadow-2xl"
	backdropBackground="bg-tertiary-500/25"
	backdropClasses="backdrop-blur-sm"
>
	{#snippet content()}
		<div class="flex flex-col gap-4">
			<header class="flex flex-col items-center justify-center gap-2">
				<span class="select-none text-9xl">🎉</span>
				<h1 class="h1">
					<span
						class="bg-gradient-radial from-tertiary-500 to-primary-500 box-decoration-clone bg-clip-text text-transparent"
						>Congrats!</span
					>
				</h1>
			</header>
			<p class="text-center">
				You finished {length} puzzles that was found using <code class="code">{query}</code>
			</p>
			<div class="flex items-center justify-around">
				<button onclick={() => closeModal()}><span class="underline">Close</span></button>
			</div>
		</div>
	{/snippet}
</Modal>
