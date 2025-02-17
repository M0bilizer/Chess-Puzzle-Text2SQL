<script lang="ts">
	import { Segment } from '@skeletonlabs/skeleton-svelte';
	import ThemesDescription from '$lib/components/ui/ThemesDescription.svelte';
	import OpeningTagsDescription from '$lib/components/ui/OpeningTagsDescription.svelte';

	let page = $state('themes');
</script>

<div
	class="card h-max w-full max-w-md border-[1px] px-4 pb-2 pt-4 text-center shadow-xl border-surface-200-800 preset-filled-surface-100-900"
>
	<div class="flex flex-col justify-center gap-2">
		{#if page === 'themes'}
			<ThemesDescription />
		{:else}
			<OpeningTagsDescription />
		{/if}
		<div class="ml-auto block">
			<Segment
				bind:value={page}
				base="inline-flex items-stretch overflow-hidden flex-row gap-2 justify-center"
				background=""
				indicatorBg="top-[var(--top)] left-[var(--left)] w-[var(--width)] h-[var(--height)] preset-filled-primary-500 rounded"
				indicatorText="preset-primary-50-950"
			>
				<Segment.Item value="themes" labelBase="text-sm">Themes</Segment.Item>
				<Segment.Item value="openings" labelBase="text-sm">Openings</Segment.Item>
			</Segment>
		</div>
	</div>
</div>
