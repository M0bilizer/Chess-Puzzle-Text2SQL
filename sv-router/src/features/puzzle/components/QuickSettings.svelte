<script lang="ts">
	import { SETTINGS_PATHS } from '@/features/settings/route';
	import { Popover, Portal, Switch } from '@skeletonlabs/skeleton-svelte';
	import TablerMenu2 from '~icons/tabler/menu-2';
	import TablerArrowUpRight from '~icons/tabler/arrow-up-right';
	import type { Preferences } from '@/features/settings/preferences-state';

	type Props = {
		preferences: Preferences;
	};
	let { preferences = $bindable() }: Props = $props();
</script>

<Popover positioning={{ placement: 'top-end' }}>
	<Popover.Trigger class="btn-icon data-[state=open]:bg-surface-200-800">
		<TablerMenu2 />
	</Popover.Trigger>
	<Portal>
		<Popover.Positioner>
			<Popover.Content class="flex min-w-[310px] flex-col gap-4 card bg-surface-200-800 p-4">
				<!-- [TODO]: switch orientation and store in localStorage -->
				<Switch
					checked={preferences.flipOrientation}
					onCheckedChange={() => (preferences.flipOrientation = !preferences.flipOrientation)}
				>
					<Switch.Control class="data-[state=unchecked]:bg-surface-300-700">
						<Switch.Thumb />
					</Switch.Control>
					<Switch.Label>Switch Orientation</Switch.Label>
					<Switch.HiddenInput />
				</Switch>
				<Switch
					checked={preferences.muted}
					onCheckedChange={() => (preferences.muted = !preferences.muted)}
				>
					<Switch.Control class="data-[state=unchecked]:bg-surface-300-700">
						<Switch.Thumb />
					</Switch.Control>
					<Switch.Label>Mute sound</Switch.Label>
					<Switch.HiddenInput />
				</Switch>
				<hr class="hr border-surface-800-200" />
				<a href={SETTINGS_PATHS.SETTINGS} class="btn items-center gap-2 self-start hover:underline">
					Other settings
					<TablerArrowUpRight class="size-icon" />
				</a>
				<Popover.Arrow
					class="[--arrow-background:var(--color-surface-200-800)] [--arrow-size:--spacing(2)]"
				>
					<Popover.ArrowTip />
				</Popover.Arrow>
			</Popover.Content>
		</Popover.Positioner>
	</Portal>
</Popover>
