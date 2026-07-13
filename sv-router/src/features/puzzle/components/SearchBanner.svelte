<script lang="ts">
	import HeaderIcon from '@/common/components/HeaderIcon.svelte';
	import TablerChessKnight from '~icons/tabler/chess-knight';

	type Props = {
		class?: string;
	};
	let { class: className = '' }: Props = $props();
</script>

<header class={`my-6 flex flex-col space-y-4 ${className}`}>
	<div class="flex gap-4">
		<HeaderIcon icon={TablerChessKnight} />
		<h1 class="h1">Search Puzzle</h1>
	</div>
	<p>
		A chess puzzle tool that allows you to search with natural language. The search results become a
		collection which is a list of puzzles you can play through, track, and come back to. Kinda like
		a Spotify or Youtube playlist but for chess puzzles.
	</p>
</header>
