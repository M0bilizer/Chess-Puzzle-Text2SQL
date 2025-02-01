<script lang="ts">
	import PuzzleDescription from '$lib/components/layout/PuzzleDescription.svelte';
	import NextButton from '$lib/components/layout/NextButton.svelte';
	import ThemesOrOpening from '$lib/components/layout/ThemesOrOpening.svelte';
	import { currentGame } from '$lib/stores/currentGameStore';
	import JumpBar from '$lib/components/layout/JumpBar.svelte';
</script>

{#if $currentGame.list.length}
	<div class="grid grid-rows-[auto_250px_auto] gap-4">
		<PuzzleDescription />
		<ThemesOrOpening />
		<NextButton />
		<JumpBar />
	</div>
{/if}

<style>
</style>
