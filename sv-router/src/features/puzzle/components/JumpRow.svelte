<script lang="ts">
	import TablerChevronLeftPipe from '~icons/tabler/chevron-left-pipe';
	import TablerChevronLeft from '~icons/tabler/chevron-left';
	import TablerChevronRight from '~icons/tabler/chevron-right';
	import TablerChevronRightPipe from '~icons/tabler/chevron-right-pipe';
	import QuickSettings from './QuickSettings.svelte';
	import type { Preferences } from '@/features/settings/preferences-state';

	type Props = {
		onReset: () => void;
		onBack: () => void;
		onForward: () => void;
		onEnd: () => void;
		canGoBack: boolean;
		canGoForward: boolean;
		preferences: Preferences;
	};
	let {
		onReset,
		onBack,
		onForward,
		onEnd,
		canGoBack,
		canGoForward,
		preferences = $bindable()
	}: Props = $props();
</script>

<section class="btn-group w-full justify-around">
	<button class="btn-icon" onclick={onReset} disabled={!canGoBack}
		><TablerChevronLeftPipe class="size-icon-big" /></button
	>
	<button class="btn-icon" onclick={onBack} disabled={!canGoBack}
		><TablerChevronLeft class="size-icon-big" /></button
	>
	<button class="btn-icon" onclick={onForward} disabled={!canGoForward}
		><TablerChevronRight class="size-icon-big" /></button
	>
	<button class="btn-icon" onclick={onEnd} disabled={!canGoForward}
		><TablerChevronRightPipe class="size-icon-big" /></button
	>
	<QuickSettings bind:preferences />
</section>
