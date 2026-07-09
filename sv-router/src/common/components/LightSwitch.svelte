<script lang="ts">
	import TablerSunHighFilled from '~icons/tabler/sun-high-filled';
	import TablerMoonFilled from '~icons/tabler/moon-filled';
	import { Switch } from '@skeletonlabs/skeleton-svelte';
	import { theme } from '@/features/settings/theme.svelte';

	type Props = {
		id?: string;
		name?: string;
	};
	let { id, name }: Props = $props();

	const onCheckedChange = (event: { checked: boolean }) => {
		theme.setTheme(event.checked);
	};
</script>

<Switch {name} checked={theme.isDarkMode} {onCheckedChange} class="btn">
	<Switch.Control {id}>
		<Switch.Thumb>
			<Switch.Context>
				{#snippet children(switch_)}
					{#if switch_().checked}
						<TablerMoonFilled class="size-3" />
					{:else}
						<TablerSunHighFilled class="size-3" />
					{/if}
				{/snippet}
			</Switch.Context>
		</Switch.Thumb>
	</Switch.Control>
	<Switch.HiddenInput />
</Switch>
