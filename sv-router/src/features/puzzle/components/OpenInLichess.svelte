<script lang="ts">
	import TablerExternalLink from '~icons/tabler/external-link';

	type Props = {
		gameUrl: string;
	};
	const { gameUrl }: Props = $props();
</script>

<a href={gameUrl} target="_blank" class="btn flex flex-row items-center gap-2 hover:underline">
	<span>Open in lichess</span>
	<TablerExternalLink class="size-icon" />
</a>
