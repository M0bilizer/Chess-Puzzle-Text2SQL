<script>
	import { AppBar } from '@skeletonlabs/skeleton-svelte';
	import TablerHome from '~icons/tabler/home';
	import TablerSettings from '~icons/tabler/settings';
	import TablerInfoCircle from '~icons/tabler/info-circle';
	import { p, ROUTES } from '@/router';
	import LightSwitch from '@/common/components/LightSwitch.svelte';
</script>

<AppBar class="h-[77px]">
	<AppBar.Toolbar class="grid-cols-[auto_1fr_auto]">
		<AppBar.Lead>
			<a href={p(ROUTES.ROOT)} data-preload>
				<button type="button" class="btn-icon btn-icon-lg hover:preset-tonal">
					<TablerHome />
				</button>
			</a>
		</AppBar.Lead>
		<AppBar.Headline>
			<p class="text-2xl">Chess Search</p>
		</AppBar.Headline>
		<AppBar.Trail>
			<LightSwitch />
			<a href={p(ROUTES.ROOT)} class="btn flex items-center gap-2 hover:underline">
				<TablerInfoCircle class="size-4" />
				<span>About</span>
			</a>
			<a href={p(ROUTES.ROOT)} class="btn flex items-center gap-2 hover:underline">
				<TablerSettings class="size-4" />
				<span>Settings</span>
			</a>
		</AppBar.Trail>
	</AppBar.Toolbar>
</AppBar>
