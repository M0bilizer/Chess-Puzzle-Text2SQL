<script lang="ts">
	import TablerAlertTriangleFilled from 'virtual:icons/tabler/alert-triangle-filled';
	import { fade } from 'svelte/transition';

	let {
		error = null,
		title = 'Error',
		dismissible = true,
		onDismiss
	}: {
		error?: string | null;
		title?: string;
		dismissible?: boolean;
		onDismiss?: () => void;
	} = $props();

	function dismiss() {
		if (onDismiss) {
			onDismiss();
		}
	}
</script>

{#if error}
	<div
		in:fade={{ duration: 200 }}
		out:fade={{ duration: 150 }}
		class="card preset-tonal-error grid grid-cols-1 items-center gap-4 p-4 lg:grid-cols-[auto_1fr_auto]"
		role="alert"
	>
		<TablerAlertTriangleFilled class="size-4 text-error-500" />
		<div>
			<p class="font-bold">{title}</p>
			<p class="text-xs opacity-60">{error}</p>
		</div>
		{#if dismissible}
			<button onclick={dismiss} class="btn preset-tonal hover:preset-filled">
				Dismiss
			</button>
		{/if}
	</div>
{/if}