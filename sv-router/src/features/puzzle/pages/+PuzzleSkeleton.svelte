<script lang="ts">
	import MainWithAsidePage from '@/common/components/MainWithAsidePage.svelte';
	import { Chessground } from 'svelte5-chessground';
</script>

<MainWithAsidePage>
	<main class="space-y-0 lg:space-y-4">
		<!-- Add a div so the space-y-4 would work properly -->
		<div>
			<Chessground fen="8/8/8/8/8/8/8/8" />
		</div>
		<div class="hidden h-20 placeholder animate-pulse md:block"></div>
	</main>
	<aside>
		<div class="aspect-square placeholder animate-pulse"></div>
	</aside>
</MainWithAsidePage>
