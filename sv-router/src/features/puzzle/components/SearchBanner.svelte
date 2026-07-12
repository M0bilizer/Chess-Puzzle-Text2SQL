<script lang="ts">
	import TablerChessKnight from '~icons/tabler/chess-knight';

	type Props = {
		class?: string;
	}
	let {class: className = ''}: Props = $props();
</script>

<header class={`flex items-center space-x-4 justify-center my-12 ${className}`}>
		<div class="relative aspect-square size-12 lg:size-16">
			<div class="absolute inset-0 -z-10 rotate-6 rounded-sm preset-filled-surface-100-900"></div>
			<div class="absolute inset-0 -z-20 -rotate-3 rounded-sm preset-filled-surface-200-800"></div>
			<TablerChessKnight class="relative z-10 size-12 text-success-900-100 lg:size-16" />
		</div>

	<h1 class="h1">Search Puzzle</h1>
</header>
