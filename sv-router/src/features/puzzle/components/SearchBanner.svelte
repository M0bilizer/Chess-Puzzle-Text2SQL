<script lang="ts">
	import TablerChessKnight from '~icons/tabler/chess-knight';
</script>

<header class="grid grid-cols-1 place-items-center space-y-6 space-x-12 lg:grid-cols-[256px_1fr]">
	<!-- Left side with tilted rectangle -->
	<section class="place-self-center">
		<div class="relative aspect-square size-48 lg:size-64">
			<!-- Tilted rectangle background -->
			<div class="absolute inset-0 -z-10 rotate-6 rounded-2xl preset-filled-surface-100-900"></div>
			<!-- Second layer for depth -->
			<div class="absolute inset-0 -z-20 -rotate-3 rounded-2xl preset-filled-surface-200-800"></div>
			<TablerChessKnight class="relative z-10 size-48 text-success-900-100 lg:size-64" />
		</div>
	</section>

	<!-- Right side -->
	<section class="text-center lg:space-y-6 lg:text-left">
		<h1 class=" text-5xl font-bold">Chess Puzzle</h1>
		<p class="max-w-2xl text-xl text-tertiary-800-200">
			Search chess puzzles with natural language.
		</p>
	</section>
</header>
