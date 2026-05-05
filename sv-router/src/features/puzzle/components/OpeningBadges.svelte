<script lang="ts">
	type Props = {
		openings: string;
	};
	let { openings }: Props = $props();

	const badges = $derived(openings.split(' ').map((it) => it.replace(/_/g, ' ')));
</script>

<dd class="space-x-2">
	{#each badges as badge, i (badge + i)}
		<span class="badge inline-block preset-filled lowercase first-letter:uppercase">
			{badge}
		</span>
	{/each}
</dd>
