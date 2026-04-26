<script lang="ts">
	import { AppBar } from '@skeletonlabs/skeleton-svelte';
	import TablerHome from '~icons/tabler/home';
	import TablerSettings from '~icons/tabler/settings';
	import TablerInfoCircle from '~icons/tabler/info-circle';
	import { PATHS } from '@/router';
	import LightSwitch from '@/common/components/LightSwitch.svelte';
</script>

<AppBar class="h-[77px]">
	<AppBar.Toolbar class="grid-cols-[auto_1fr_auto]">
		<AppBar.Lead>
			<a href={PATHS.HOME} data-preload>
				<button type="button" class="btn-icon btn-icon-lg hover:preset-tonal">
					<TablerHome />
				</button>
			</a>
		</AppBar.Lead>
		<AppBar.Headline>
			<p class="text-2xl">Chess Search</p>
		</AppBar.Headline>
		<AppBar.Trail>
			<a href={PATHS.ABOUT} class="btn flex items-center gap-2 hover:underline">
				<TablerInfoCircle class="size-icon" />
				<span>About</span>
			</a>
			<a href={PATHS.SETTINGS} class="btn flex items-center gap-2 hover:underline">
				<TablerSettings class="size-icon" />
				<span>Settings</span>
			</a>
			<span class="vr h-8"></span>
			<LightSwitch />
		</AppBar.Trail>
	</AppBar.Toolbar>
</AppBar>
