<script lang="ts">
	import apiStore from '$lib/stores/apiStore';
</script>

<div class="ml-auto mr-auto flex h-full w-1/3 flex-col items-center gap-4 rounded-lg pl-4 pt-40">
	<ul>
		{#each $apiStore.puzzleData as puzzle}
			<li>{puzzle.puzzleId}</li>
		{/each}
	</ul>
</div>
