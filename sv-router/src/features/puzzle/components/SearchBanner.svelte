<script>
	import TablerChessKnight from '~icons/tabler/chess-knight';
</script>

<header class="my-12 grid grid-cols-1 xl:grid-cols-[256px_1fr] gap-12 items-center">
	<!-- Left side with tilted rectangle -->
	<div class="flex justify-center items-center">
		<div class="relative size-48 md:size-64 aspect-square">
			<!-- Tilted rectangle background -->
			<div class="absolute inset-0 preset-filled-surface-100-900 rounded-2xl rotate-6 -z-10"></div>
			<!-- Second layer for depth -->
			<div class="absolute inset-0 preset-filled-surface-200-800 rounded-2xl -rotate-3 -z-20"></div>
			<TablerChessKnight class="size-48 md:size-64 relative z-10 text-success-900-100" />
		</div>
	</div>

	<!-- Right side -->
	<div class="space-y-6 text-center xl:text-left">
		<h1 class="text-4xl md:text-5xl lg:text-6xl font-bold">
			Chess Puzzle Finder
		</h1>
		<p class="text-lg md:text-xl text-gray-600 dark:text-gray-300 max-w-2xl mx-auto xl:mx-0">
			Search thousands of chess puzzles by opening, theme, or position. Find your next challenge from our database of
			tactical puzzles.
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

    .animate-spin-slow {
        animation: spin-slow 8s linear infinite;
    }
</style>