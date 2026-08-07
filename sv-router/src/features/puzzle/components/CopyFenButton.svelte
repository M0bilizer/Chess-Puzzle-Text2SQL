<script lang="ts">
	import Clipboard from '@/common/components/Clipboard.svelte';
	import TablerCheck from '~icons/tabler/check';
	import TablerClipboard from '~icons/tabler/clipboard';

	type Props = {
		fen: string;
		class?: string;
	};
	let { fen, class: className }: Props = $props();
</script>

<Clipboard value={fen}>
	{#snippet children({ copied, copy })}
		<button onclick={copy} class={`btn gap-2 active:scale-95 ${className}`}>
			{#if copied}
				<TablerCheck />
				Fen copied!
			{:else}
				<TablerClipboard />
				Copy FEN
			{/if}
		</button>
	{/snippet}
</Clipboard>
