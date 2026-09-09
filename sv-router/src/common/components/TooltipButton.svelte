<script lang="ts">
	import { Portal, Tooltip } from '@skeletonlabs/skeleton-svelte';
	import type { Snippet } from 'svelte';

	type Props = {
		button: Snippet;
		message: string;
	};
	const { button, message }: Props = $props();
</script>

<Tooltip>
	<Tooltip.Trigger>
		{@render button()}
	</Tooltip.Trigger>
	<Portal>
		<Tooltip.Positioner>
			<Tooltip.Content class="card p-2 preset-filled-surface-200-800"
				>{message}
				<Tooltip.Arrow
					class="[--arrow-size:--spacing(2)] [--arrow-background:var(--color-surface-200-800)]"
				>
					<Tooltip.ArrowTip />
				</Tooltip.Arrow>
			</Tooltip.Content>
		</Tooltip.Positioner>
	</Portal>
</Tooltip>
