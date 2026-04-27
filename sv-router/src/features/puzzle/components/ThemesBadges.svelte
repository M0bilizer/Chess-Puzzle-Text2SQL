<script lang="ts">
	type Props = {
		themes: string;
	};
	let { themes }: Props = $props();

	const badges = $derived(
		themes.split(' ').map((it) =>
			it
				// Insert space before capital letters (camelCase)
				.replace(/([A-Z])/g, ' $1')
				// Insert space before numbers (mateIn1 -> mate In 1)
				.replace(/([a-z])(\d)/gi, '$1 $2')
				// Insert space after numbers (if followed by word)
				.replace(/(\d)([a-z])/gi, '$1 $2')
				// Convert to lowercase
				.toLowerCase()
		)
	);
</script>

<dd class="space-x-2">
	{#each badges as badge, i (badge + i)}
		<span class="badge inline-block preset-filled lowercase first-letter:uppercase">
			{badge}
		</span>
	{/each}
</dd>
