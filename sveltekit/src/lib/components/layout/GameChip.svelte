<script lang="ts">
	import Fa6SolidCheck from 'virtual:icons/fa6-solid/check';
	import { loadGame } from '$lib/utils/storeUtils';
	let { type, index } = $props();

	function handleClick() {
		loadGame(index);
	}
</script>

{#if type === 'won'}
	<button type="button" onclick={handleClick} class="chip preset-filled-primary-500">
		<Fa6SolidCheck />
	</button>
{:else if type === 'progress'}
	<button type="button" onclick={handleClick} class="chip preset-filled-primary-500">
		<Fa6SolidCheck class="text-primary-500" />
	</button>
{:else if type === 'locked'}
	<button type="button" onclick={handleClick} class="chip preset-filled-secondary-500">
		<Fa6SolidCheck class="text-secondary-500" />
	</button>
{/if}

<style>
</style>
