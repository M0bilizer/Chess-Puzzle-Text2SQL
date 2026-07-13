<script lang="ts">
	import { AppBar } from '@skeletonlabs/skeleton-svelte';
	import { PATHS } from '@/router';
	import LightSwitch from '@/common/components/LightSwitch.svelte';
</script>

<AppBar class="mb-2 h-[77px] bg-transparent">
	<AppBar.Toolbar class="flex h-full justify-end">
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
