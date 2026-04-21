<script>
	import TablerChessKnight from '~icons/tabler/chess-knight';
</script>

<header class="my-12 grid grid-cols-1 items-center gap-12 xl:grid-cols-[256px_1fr]">
	<!-- Left side with tilted rectangle -->
	<div class="flex items-center justify-center">
		<div class="relative aspect-square size-48 md:size-64">
			<!-- Tilted rectangle background -->
			<div class="absolute inset-0 -z-10 rotate-6 rounded-2xl preset-filled-surface-100-900"></div>
			<!-- Second layer for depth -->
			<div class="absolute inset-0 -z-20 -rotate-3 rounded-2xl preset-filled-surface-200-800"></div>
			<TablerChessKnight class="relative z-10 size-48 text-success-900-100 md:size-64" />
		</div>
	</div>

	<!-- Right side -->
	<div class="space-y-6 text-center xl:text-left">
		<h1 class="text-4xl font-bold md:text-5xl lg:text-6xl">Chess Puzzle Finder</h1>
		<p class="mx-auto max-w-2xl text-lg text-gray-600 md:text-xl xl:mx-0 dark:text-gray-300">
			Search thousands of chess puzzles by opening, theme, or position. Find your next challenge
			from our database of tactical puzzles.
		</p>
	</div>
</header>

<style>
	@keyframes spin-slow {
		from {
			transform: rotate(0deg);
		}
		to {
			transform: rotate(360deg);
		}
	}
</style>
