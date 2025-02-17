<script lang="ts">
	import ChessRight from '$lib/components/layout/ChessRight.svelte';
	import ChessLeft from '$lib/components/layout/ChessLeft.svelte';
	import ChessBoard from '$lib/components/layout/ChessBoard.svelte';
	import ChipBar from '$lib/components/layout/ChipBar.svelte';
</script>

<div
	class="container mx-auto grid grid-cols-1 gap-x-4 py-4 sm:grid-cols-[250px_minmax(0px,_1fr)_300px]"
>
	<aside class="order-3 sm:order-1">
		<ChessLeft />
	</aside>
	<main id="_top" class="flex-start order-2 flex flex-col gap-2">
		<ChessBoard />
		<ChipBar />
	</main>
	<aside class="order-1 sm:order-3">
		<ChessRight />
	</aside>
</div>

<style>
</style>
