<script lang="ts">
	import ChessBoard from '$lib/components/layout/ChessBoard.svelte';
	import ChipBar from '$lib/components/layout/ChipBar.svelte';
	import QueryDisplay from '$lib/components/ui/QueryDisplay.svelte';
	import RecentSearches from '$lib/components/ui/RecentSearches.svelte';
	import PuzzleDescription from '$lib/components/ui/PuzzleDescription.svelte';
	import NextButton from '$lib/components/ui/NextButton.svelte';
	import ThemesOrOpening from '$lib/components/ui/ThemesOrOpening.svelte';
	import { currentGame } from '$lib/stores/currentGameStore';
	import JumpBar from '$lib/components/ui/JumpBar.svelte';
	import MobileNextButton from '$lib/components/ui/MobileNextButton.svelte';
	import MobileInfoCard from '$lib/components/ui/MobileInfoCard.svelte';
</script>

<div
	class="w-full mx-auto grid grid-cols-1 gap-x-4 gap-y-2 px-4 py-4 md:grid-cols-[minmax(0px,_1fr)_250px] md:w-4/6 xl:grid-cols-[300px_minmax(0px,_1fr)_300px] xl:w-1/2"
>
	<aside class="hidden xl:block">
		<div class="desktop grid-rows-[auto_auto] gap-4">
			<QueryDisplay />
			<RecentSearches />
		</div>
	</aside>
	<main id="_top" class="flex-start flex flex-col gap-2">
		<ChessBoard />
		<ChipBar />
	</main>
	<aside>
		<div class="desktop grid-rows-[auto_250px_auto_auto] gap-4">
			{#if $currentGame.list.length}
				<PuzzleDescription />
				<ThemesOrOpening />
				<NextButton />
				<JumpBar />
			{/if}
		</div>
		<div class="tablet grid-rows-[auto_auto_auto] gap-4">
			{#if $currentGame.list.length}
				<PuzzleDescription />
				<NextButton />
				<JumpBar />
			{/if}
		</div>
	</aside>
	<aside>
		<div class="tablet flex flex-col gap-2">
			<QueryDisplay />
			<RecentSearches />
		</div>
		<div class="mobile flex max-w-full flex-col gap-2">
			{#if $currentGame.list.length}
				<JumpBar />
				<MobileNextButton />
				<MobileInfoCard />
			{:else}
				<RecentSearches />
			{/if}
		</div>
	</aside>
	<aside>
		<div class="tablet flex flex-col gap-4">
			{#if $currentGame.list.length}
				<ThemesOrOpening />
			{/if}
		</div>
	</aside>
</div>

<style lang="postcss">
	.mobile {
		@apply grid md:hidden xl:hidden;
	}

	.tablet {
		@apply hidden md:grid xl:hidden;
	}

	.desktop {
		@apply hidden md:hidden xl:grid;
	}
</style>
