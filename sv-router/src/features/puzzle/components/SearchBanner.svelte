<script lang="ts">
	type Props = {
		class?: string;
	};
	let { class: className = '' }: Props = $props();
</script>

<header class={`my-6 flex flex-col space-y-4 ${className}`}>
	<div class="flex justify-center gap-4 md:justify-start">
		<h1 class="text-center h1">Search for Chess Puzzle</h1>
	</div>
	<p>
		The search results become a collection which is a list of puzzles you can play through, track,
		and come back to. Kinda like a Spotify or Youtube playlist but for chess puzzles.
	</p>
</header>
