<script lang="ts">
	import Chessica from '@/assets/Chessica.svelte';
	import LightSwitch from '@/common/components/LightSwitch.svelte';
	import { p } from '@/router';
	import { AppBar } from '@skeletonlabs/skeleton-svelte';
</script>

<AppBar class="mb-2 h-[77px] hidden md:block content-center">
	<AppBar.Toolbar class="grid-cols-[auto_1fr_auto] place-items-stretch *:flex *:items-center">
		<AppBar.Lead>
			<a href={p('/')} class="flex flex-row items-center" data-preload>
				<Chessica size={36} color="var(--color-surface-700-300)" />
				<span class="preset-typo-subtitle inline">AskChessica</span>
			</a>
		</AppBar.Lead>
		<AppBar.Headline></AppBar.Headline>
		<AppBar.Trail>
			<a href={p('/about')} class="btn hover:underline"> About </a>
			<a href={p('/settings')} class="btn hover:underline"> Settings </a>
			<span class="vr h-8"></span>
			<LightSwitch />
		</AppBar.Trail>
	</AppBar.Toolbar>
</AppBar>
