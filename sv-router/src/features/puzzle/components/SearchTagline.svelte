<script lang="ts">
	type Props = {
		class?: string;
	};

	let { class: className = '' }: Props = $props();
</script>

<p class={`text-center text-surface-600-400 ${className}`}>The search engine for chess puzzles</p>
