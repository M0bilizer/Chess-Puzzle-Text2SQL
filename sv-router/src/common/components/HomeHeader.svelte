<script lang="ts">
	import LightSwitch from '@/common/components/LightSwitch.svelte';
	import { p } from '@/router';
	import { AppBar } from '@skeletonlabs/skeleton-svelte';
</script>

<AppBar class="mb-2 h-[77px] bg-transparent">
	<AppBar.Toolbar class="flex h-full justify-end">
		<AppBar.Trail>
			<a href={p('/about')} class="btn flex items-center gap-2 hover:underline">
				<span>About</span>
			</a>
			<a href={p('/settings')} class="btn flex items-center gap-2 hover:underline">
				<span>Settings</span>
			</a>
			<span class="vr h-8"></span>
			<LightSwitch />
		</AppBar.Trail>
	</AppBar.Toolbar>
</AppBar>
