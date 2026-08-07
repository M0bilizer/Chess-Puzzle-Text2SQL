<script lang="ts">
	import TablerExternalLink from '~icons/tabler/external-link';

	type Props = {
		gameUrl: string;
		class?: string;
	};
	const { gameUrl, class: className }: Props = $props();
</script>

<a
	href={gameUrl}
	target="_blank"
	class={`btn anchor gap-2 hover:underline [&.btn-xs]:text-xs ${className}`}
>
	Open in lichess
	<TablerExternalLink />
</a>
