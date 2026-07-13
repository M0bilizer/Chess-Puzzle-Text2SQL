<script lang="ts">
	import { AppBar } from '@skeletonlabs/skeleton-svelte';
	import TablerHome from '~icons/tabler/home';
	import { PATHS } from '@/router';
	import LightSwitch from '@/common/components/LightSwitch.svelte';
</script>

<AppBar class="mb-2 h-[77px]">
	<AppBar.Toolbar class="grid-cols-[auto_1fr_auto]">
		<AppBar.Lead>
			<a href={PATHS.HOME} class="btn-icon btn-icon-lg hover:preset-tonal" data-preload>
				<TablerHome class="text-surface-700-300"/>
			</a>
		</AppBar.Lead>
		<AppBar.Headline>
			<p class="preset-typo-subtitle">Search Puzzle</p>
		</AppBar.Headline>
		<AppBar.Trail>
			<a href={PATHS.ABOUT} class="btn flex items-center gap-2 hover:underline">
				<span>About</span>
			</a>
			<a href={PATHS.SETTINGS} class="btn flex items-center gap-2 hover:underline">
				<span>Settings</span>
			</a>
			<span class="vr h-8"></span>
			<LightSwitch />
		</AppBar.Trail>
	</AppBar.Toolbar>
</AppBar>
