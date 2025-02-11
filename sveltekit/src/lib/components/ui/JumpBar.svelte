<script lang="ts">
	import Fa6SolidBackwardFast from 'virtual:icons/fa6-solid/backward-fast';
	import Fa6SolidBackwardStep from 'virtual:icons/fa6-solid/backward-step';
	import Fa6SolidForwardStep from 'virtual:icons/fa6-solid/forward-step';
	import Fa6SolidForwardFast from 'virtual:icons/fa6-solid/forward-fast';
	import {
		decrementJump,
		endJump,
		incrementJump,
		isAtStart,
		isInJump,
		jump,
		reset
	} from '$lib/stores/jumpStore';
	import BurgerMenu from '$lib/components/ui/BurgerMenu.svelte';

	let disableUndo = $state(false);
	let isJumping = $state(false);

	jump.subscribe(() => {
		isJumping = isInJump();
		disableUndo = isAtStart();
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
	<button onclick={() => handleFastUndo()} disabled={disableUndo}>
		<Fa6SolidBackwardFast class="size-4 text-tertiary-500" />
	</button>
	<button onclick={() => handleUndo()} disabled={disableUndo}>
		<Fa6SolidBackwardStep class="size-6 text-tertiary-500" />
	</button>
	<button onclick={() => handleRedo()} disabled={!isJumping}>
		<Fa6SolidForwardStep class="size-6 text-tertiary-500" />
	</button>
	<button onclick={() => handleFastRedo()} disabled={!isJumping}>
		<Fa6SolidForwardFast class="size-6 text-tertiary-500" />
	</button>
	<BurgerMenu />
</div>
