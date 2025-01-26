<script lang="ts">
	import PuzzleDescription from '$lib/components/layout/PuzzleDescription.svelte';
	import NextButton from '$lib/components/layout/NextButton.svelte';
	import ThemesOrOpening from '$lib/components/layout/ThemesOrOpening.svelte';
	import { currentGame } from '$lib/stores/currentGameStore';
</script>

{#if $currentGame.list}
	<div class="flex flex-col gap-5 px-2">
		<PuzzleDescription />
		<ThemesOrOpening />
		<NextButton />
	</div>
{/if}

<style>
</style>
