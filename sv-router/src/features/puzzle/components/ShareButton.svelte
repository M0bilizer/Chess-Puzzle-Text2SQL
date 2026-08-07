<script lang="ts">
	import Clipboard from '@/common/components/Clipboard.svelte';
	import TablerCheck from '~icons/tabler/check';
	import TablerShare from '~icons/tabler/share';

	type Props = {
		url?: string;
		title?: string;
		text?: string;
		class?: string;
	};
	let { url = window.location.href, title, text, class: className }: Props = $props();

	async function handleShare() {
		if (navigator.share) {
			try {
				await navigator.share({ title, text, url });
			} catch (_err) {
				// User cancelled or error
			}
		}
	}
</script>

{#if navigator.canShare && navigator.canShare({ title, text, url })}
	<!-- Use native share on mobile -->
	<button onclick={handleShare} class={`btn gap-2 active:scale-95 ${className}`}>
		<TablerShare />
		Share
	</button>
{:else}
	<!-- Fallback to clipboard on desktop -->
	<Clipboard value={url}>
		{#snippet children({ copied, copy })}
			<button onclick={copy} class={`btn gap-2 active:scale-95 ${className}`}>
				{#if copied}
					<TablerCheck />
					Link copied!
				{:else}
					<TablerShare />
					Share
				{/if}
			</button>
		{/snippet}
	</Clipboard>
{/if}
