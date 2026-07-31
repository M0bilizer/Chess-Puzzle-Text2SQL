<script lang="ts">
	import ContentPage from '@/common/components/ContentPage.svelte';
</script>

<ContentPage class="space-y-6">
	<header class="space-y-4">
		<h1 class="text-left h1">About</h1>
	</header>
	<hr class="hr border-surface-200-800" />
	<p>
		Hey! This website lets you find chess puzzles using natural language, powered by Text2SQL. When
		you search in plain english, the system reads your words and figures out what you&rsquo;re
		looking for (like piece types, move patterns, or checkmate conditions). Then it looks for
		matching puzzles in the database and presents them for your collection!
	</p>
	<p>
		I built this as my final year undergraduate project and I&rsquo;m proud to say it received an
		&ldquo;A&rdquo; grade for its innovation and effort. Since then, I&rsquo;ve kept working on it
		in hopes that it&rsquo;ll be useful for other users too!
	</p>
	<p>
		Check out the development journey:
		<a class="anchor" href="https://github.com/M0bilizer/Chess-Puzzle-Text2SQL"
			>https://github.com/M0bilizer/Chess-Puzzle-Text2SQL</a
		>
	</p>

	<p class="text-right">
		— <a class="anchor" href="https://github.com/M0bilizer">M0bilizer</a>
	</p>
</ContentPage>
