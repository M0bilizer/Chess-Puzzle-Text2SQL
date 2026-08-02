<script lang="ts">
	import LightSwitch from '@/common/components/LightSwitch.svelte';
	import { p } from '@/router';
	import { AppBar } from '@skeletonlabs/skeleton-svelte';
	import TablerHome from '~icons/tabler/home';
</script>

<AppBar class="mb-2 h-[77px] hidden md:block content-center">
	<AppBar.Toolbar class="grid-cols-[auto_1fr_auto] place-items-stretch *:flex *:items-center">
		<AppBar.Lead>
			<a href={p('/')} class="btn-icon btn-icon-lg hover:preset-tonal" data-preload>
				<TablerHome class="text-surface-700-300" />
			</a>
		</AppBar.Lead>
		<AppBar.Headline>
			<p class="preset-typo-subtitle">Search Puzzle</p>
		</AppBar.Headline>
		<AppBar.Trail>
			<a href={p('/about')} class="btn hover:underline"> About </a>
			<a href={p('/settings')} class="btn hover:underline"> Settings </a>
			<span class="vr h-8"></span>
			<LightSwitch />
		</AppBar.Trail>
	</AppBar.Toolbar>
</AppBar>
