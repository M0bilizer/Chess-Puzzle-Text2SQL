<script>
	import { AppBar } from '@skeletonlabs/skeleton-svelte';
	import TablerHome from '~icons/tabler/home';
	import TablerMenu2 from '~icons/tabler/menu-2';
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
			<button type="button" class="btn-icon hover:preset-tonal">
				<TablerMenu2 />
			</button>
		</AppBar.Trail>
	</AppBar.Toolbar>
</AppBar>
