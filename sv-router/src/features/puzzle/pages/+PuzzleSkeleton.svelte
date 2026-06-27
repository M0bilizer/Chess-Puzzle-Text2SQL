<script lang="ts">
	import MainWithAsidePage from '@/common/components/MainWithAsidePage.svelte';
	import { onDestroy, onMount } from 'svelte';
	import { Chessground } from 'svelte5-chessground';

	let height: number | undefined = $state(0);
	let resizeObserver: ResizeObserver;
	let chessboard: HTMLElement;
	onMount(() => {
		function updateHeight() {
			height = chessboard?.clientHeight;
		}
		updateHeight();
		resizeObserver = new ResizeObserver(() => {
			updateHeight();
		});
		resizeObserver.observe(chessboard);
	});
	onDestroy(() => {
		resizeObserver?.disconnect();
	});
</script>

<MainWithAsidePage>
	<aside style:height="{height}px" class="max-w-[330px] min-w-[256px]">
		<div class="h-full placeholder animate-pulse"></div>
	</aside>
	<main class="space-y-0 lg:space-y-4">
		<!-- Add a div so the space-y-4 would work properly -->
		<div bind:this={chessboard}>
			<Chessground fen="8/8/8/8/8/8/8/8" />
		</div>
		<div class="hidden h-20 placeholder animate-pulse md:block"></div>
	</main>
	<aside>
		<div class="aspect-square placeholder animate-pulse"></div>
	</aside>
</MainWithAsidePage>
