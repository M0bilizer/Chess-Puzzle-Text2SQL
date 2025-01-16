<script lang="ts">
	import ChessRight from '$lib/components/layout/ChessRight.svelte';
	import ChessLeft from '$lib/components/layout/ChessLeft.svelte';
	import ChessBoard from '$lib/components/layout/ChessBoard.svelte';
</script>

<div
	class="container mx-auto grid grid-cols-1 gap-x-4 py-4 sm:grid-cols-[250px_minmax(0px,_1fr)_300px]"
>
	<aside>
		<ChessLeft />
	</aside>
	<main id="_top" class="flex-start flex flex-col">
		<ChessBoard />
	</main>
	<aside>
		<ChessRight />
	</aside>
</div>

<style>
</style>
