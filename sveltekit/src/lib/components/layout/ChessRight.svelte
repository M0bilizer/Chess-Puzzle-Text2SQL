<script lang="ts">
	import PuzzleDescription from '$lib/components/ui/PuzzleDescription.svelte';
	import NextButton from '$lib/components/ui/NextButton.svelte';
	import ThemesOrOpening from '$lib/components/ui/ThemesOrOpening.svelte';
	import { currentGame } from '$lib/stores/currentGameStore';
	import JumpBar from '$lib/components/ui/JumpBar.svelte';
</script>

{#if $currentGame.list.length}
	<div class="grid grid-rows-[auto_250px_auto] gap-4">
		<PuzzleDescription />
		<ThemesOrOpening />
		<NextButton />
		<JumpBar />
	</div>
{/if}
