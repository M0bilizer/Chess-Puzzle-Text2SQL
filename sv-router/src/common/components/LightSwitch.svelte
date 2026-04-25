<script lang="ts">
	import TablerSunHighFilled from '~icons/tabler/sun-high-filled';
	import TablerMoonFilled from '~icons/tabler/moon-filled';
	import { Switch } from '@skeletonlabs/skeleton-svelte';

	let checked = $state(false);

	$effect(() => {
		const mode = localStorage.getItem('mode') || 'light';
		checked = mode === 'dark';
		document.documentElement.setAttribute('data-mode', checked ? 'dark' : 'light');
	});

	const onCheckedChange = (event: { checked: boolean }) => {
		const mode = event.checked ? 'dark' : 'light';
		document.documentElement.setAttribute('data-mode', mode);
		localStorage.setItem('mode', mode);
		checked = event.checked;
	};
</script>

<svelte:head>
	<script>
		document.documentElement.setAttribute('data-mode', localStorage.getItem('mode') || 'dark');
	</script>
</svelte:head>

<Switch {checked} {onCheckedChange} class="btn">
	<Switch.Control>
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
