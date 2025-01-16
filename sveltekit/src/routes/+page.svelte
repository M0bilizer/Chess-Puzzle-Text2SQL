<script lang="ts">
	import ChessLeftCard from '$lib/components/layout/ChessLeftCard.svelte';
	import ChessRightCard from '$lib/components/layout/ChessRightCard.svelte';
	import ChessBoard from '$lib/components/layout/ChessBoard.svelte';
</script>

<div
	class="container mx-auto grid grid-cols-1 gap-x-4 py-4 sm:grid-cols-[250px_minmax(0px,_1fr)_300px]"
>
	<aside>
		<ChessLeftCard />
	</aside>
	<main id="_top" class="flex-start flex flex-col">
		<ChessBoard />
	</main>
	<aside>
		<ChessRightCard />
	</aside>
</div>

<style>
</style>
