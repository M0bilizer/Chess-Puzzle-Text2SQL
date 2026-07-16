<script lang="ts">
	import TablerAlertTriangleFilled from 'virtual:icons/tabler/alert-triangle-filled';
	import { fade } from 'svelte/transition';

	type Props = {
		error?: string | null;
		title?: string;
		dismissible?: boolean;
		onDismiss?: () => void;
		class?: string;
	};

	let {
		error = null,
		title = 'Error',
		dismissible = true,
		onDismiss,
		class: className = ''
	}: Props = $props();

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
		class={`grid grid-cols-[auto_1fr_auto] items-center gap-4 card border border-error-400-600 preset-tonal-error p-4 ${className}`}
		role="alert"
	>
		<TablerAlertTriangleFilled class="size-icon-big text-error-500" />
		<div class="flex flex-col gap-1">
			<strong>{title}</strong>
			<em class="opacity-60">{error}</em>
		</div>
		{#if dismissible}
			<button onclick={dismiss} class="btn preset-filled-error-500"> Dismiss </button>
		{/if}
	</div>
{/if}
