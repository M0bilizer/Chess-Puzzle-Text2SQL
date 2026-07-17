<script lang="ts">
	import Clipboard from '@/common/components/Clipboard.svelte';
	import TablerCheck from '~icons/tabler/check';
	import TablerClipboard from '~icons/tabler/clipboard';

	type Props = {
		fen: string;
	};
	let { fen }: Props = $props();
</script>

<Clipboard value={fen}>
	{#snippet children({ copied, copy })}
		<button onclick={copy} class="btn gap-2 active:scale-95">
			{#if copied}
				<TablerCheck class="size-icon" />
				Fen copied!
			{:else}
				<TablerClipboard class="size-icon" />
				Copy FEN
			{/if}
		</button>
	{/snippet}
</Clipboard>
