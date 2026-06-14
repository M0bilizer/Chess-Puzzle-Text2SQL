<script lang="ts">
	import { Chessground } from 'svelte5-chessground';
</script>

<div
	class="group block w-80 overflow-hidden card border border-surface-200-800 preset-filled-surface-100-900 lg:w-64"
>
	<div class="thumbnail relative h-80 w-80 lg:h-64 lg:w-64">
		<Chessground fen="8/8/8/8/8/8/8/8" />
	</div>
	<div class="h-9 animate-pulse"></div>
</div>

<style>
	/* hide the coordinates */
	.thumbnail :global(.ranks),
	.thumbnail :global(.files) {
		visibility: hidden;
	}
</style>
