<script lang="ts">
	import { Portal, Tooltip } from '@skeletonlabs/skeleton-svelte';
	import ArrowBackUpIcon from '~icons/tabler/arrow-back-up';

	type Props = {
		onclick: () => void;
		disabled: boolean;
	};
	let { onclick, disabled }: Props = $props();
</script>

{#if !disabled}
	<Tooltip positioning={{ placement: 'top' }}>
		<Tooltip.Trigger
			><button class="btn-icon-sm" type="button" {onclick}>
				<ArrowBackUpIcon />
			</button></Tooltip.Trigger
		>
		<Portal>
			<Tooltip.Positioner>
				<Tooltip.Content class="card p-2 preset-filled-surface-950-50">
					<span>Reset to default</span>
					<Tooltip.Arrow
						class="[--arrow-size:--spacing(2)] [--arrow-background:var(--color-surface-950-50)]"
					>
						<Tooltip.ArrowTip />
					</Tooltip.Arrow>
				</Tooltip.Content>
			</Tooltip.Positioner>
		</Portal>
	</Tooltip>
{/if}
