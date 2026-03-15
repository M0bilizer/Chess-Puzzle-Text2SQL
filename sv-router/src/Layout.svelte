<script lang="ts">
	import { AppBar } from '@skeletonlabs/skeleton-svelte';
	import TablerHome from '~icons/tabler/home';
	import TablerMenu2 from '~icons/tabler/menu-2';
	import { p, ROUTES } from '@/router';

	let { children } = $props();
</script>

<div class="flex h-screen flex-col overflow-hidden">
	<header class="sticky top-0 z-10">
		<AppBar>
			<AppBar.Toolbar class="grid-cols-[auto_1fr_auto]">
				<AppBar.Lead>
					<a href={p(ROUTES.HOME)} data-preload>
						<button type="button" class="btn-icon btn-icon-lg hover:preset-tonal">
							<TablerHome />
						</button>
					</a>
				</AppBar.Lead>
				<AppBar.Headline>
					<p class="text-2xl">Chess Search</p>
				</AppBar.Headline>
				<AppBar.Trail>
					<button type="button" class="btn-icon hover:preset-tonal">
						<TablerMenu2 />
					</button>
				</AppBar.Trail>
			</AppBar.Toolbar>
		</AppBar>
	</header>

	<div class="container mx-auto h-screen w-screen">
		{@render children()}
	</div>

	<footer>
		<!-- Footer content -->
	</footer>
</div>
