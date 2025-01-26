<script lang="ts">
	import Fa6SolidCheck from 'virtual:icons/fa6-solid/check';
	let { type } = $props();
</script>

{#if type === 'won'}
	<button type="button" class="chip preset-filled-primary-500">
		<Fa6SolidCheck />
	</button>
{:else if type === 'progress'}
	<button type="button" class="chip preset-filled-primary-500">
		<Fa6SolidCheck class="text-primary-500" />
	</button>
{:else if type === 'locked'}
	<button type="button" class="chip preset-filled-secondary-500">
		<Fa6SolidCheck class="text-secondary-500" />
	</button>
{/if}

<style>
</style>
