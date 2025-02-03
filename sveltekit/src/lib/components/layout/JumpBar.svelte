<script lang="ts">
	import Fa6SolidBackwardFast from 'virtual:icons/fa6-solid/backward-fast';
	import Fa6SolidBackwardStep from 'virtual:icons/fa6-solid/backward-step';
	import Fa6SolidForwardStep from 'virtual:icons/fa6-solid/forward-step';
	import Fa6SolidForwardFast from 'virtual:icons/fa6-solid/forward-fast';
	import Fa6SolidBars from 'virtual:icons/fa6-solid/bars';
	import {
		decrementJump,
		endJump,
		incrementJump,
		isInJump,
		jump,
		reset
	} from '$lib/stores/jumpStore';

	let isJumping = $state(false);

	jump.subscribe(() => {
		isJumping = isInJump();
	});

	function handleFastUndo() {
		reset();
	}

	function handleUndo() {
		decrementJump();
	}

	function handleRedo() {
		incrementJump();
	}

	function handleFastRedo() {
		endJump();
	}
</script>

<div class="flex flex-row justify-around gap-2">
	<button onclick={() => handleFastUndo()}>
		<Fa6SolidBackwardFast class="size-4 text-tertiary-500" />
	</button>
	<button onclick={() => handleUndo()}>
		<Fa6SolidBackwardStep class="size-6 text-tertiary-500" />
	</button>
	<button onclick={() => handleRedo()} disabled={!isJumping}>
		<Fa6SolidForwardStep class="size-6 text-tertiary-500" />
	</button>
	<button onclick={() => handleFastRedo()} disabled={!isJumping}>
		<Fa6SolidForwardFast class="size-6 text-tertiary-500" />
	</button>
	<Fa6SolidBars class="size-4 text-tertiary-500" />
</div>

<style>
</style>
