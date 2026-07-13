<script lang="ts">
	import type { Component } from 'svelte';

	type Props = {
		icon: Component;
		class?: string;
	};
	const { icon: Icon, class: className }: Props = $props();
</script>

<div class="relative aspect-square size-12 lg:size-16">
	<div class="absolute inset-0 -z-10 rotate-6 rounded-sm preset-filled-surface-100-900"></div>
	<div class="absolute inset-0 -z-20 -rotate-3 rounded-sm preset-filled-surface-200-800"></div>
	<Icon class={`relative z-10 size-12 text-success-900-100 lg:size-16 ${className}`} />
</div>
