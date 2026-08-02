<script lang="ts">
	import LightSwitch from '@/common/components/LightSwitch.svelte';
	import { p } from '@/router';
	import { AppBar, Dialog, Portal } from '@skeletonlabs/skeleton-svelte';
	import { isActiveLink } from 'sv-router';
	import TablerHome from '~icons/tabler/home';
	import TablerMenu2 from '~icons/tabler/menu-2';
	import TablerX from '~icons/tabler/x';
</script>

<AppBar class="mb-2 h-[77px] block md:hidden content-center">
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
			<LightSwitch />
			<span class="vr h-8"></span>
			<Dialog>
				<Dialog.Trigger class="btn-icon -p-1 m-1"><TablerMenu2 /></Dialog.Trigger>
				<Portal>
					<Dialog.Backdrop
						class={[
							'fixed inset-0 z-50 bg-black/80',
							'transition-all duration-300 ease-in-out',
							'opacity-0 starting:data-[state=open]:opacity-0 data-[state=open]:opacity-100 data-[state=closed]:opacity-0'
						]}
					/>
					<Dialog.Positioner class="fixed inset-0 z-50 flex justify-end">
						<Dialog.Content
							class={[
								'h-screen card bg-surface-100-900 w-sm p-4 space-y-4 shadow-xl',
								'transition-all duration-300 ease-[cubic-bezier(0.16,1,0.3,1)]',
								'opacity-0 translate-x-full',
								'starting:data-[state=open]:opacity-0 starting:data-[state=open]:translate-x-full',
								'data-[state=open]:opacity-100 data-[state=open]:translate-x-0',
								'data-[state=closed]:opacity-0 data-[state=closed]:translate-x-full',
								'origin-right'
							]}
						>
							<header class="flex justify-between items-center">
								<Dialog.Title class="preset-typo-subtitle">Search Puzzle</Dialog.Title>
								<Dialog.CloseTrigger class="btn-icon m-1 -p-1">
									<TablerX />
								</Dialog.CloseTrigger>
							</header>
							<hr class="hr" />
							<ul class="p-2 space-y-2">
								<li>
									<a
										href={p('/')}
										data-preload
										class="flex justify-start btn hover:preset-tonal px-2"
										{@attach isActiveLink({ className: 'preset-tonal' })}
									>
										Home
									</a>
								</li>
								<li>
									<a
										href={p('/about')}
										data-preload
										class="flex justify-start btn hover:preset-tonal px-2"
										{@attach isActiveLink({ className: 'preset-tonal' })}
									>
										About
									</a>
								</li>
								<li>
									<a
										href={p('/settings')}
										data-preload
										class="flex justify-start btn hover:preset-tonal px-2"
										{@attach isActiveLink({ className: 'preset-tonal' })}
									>
										Settings
									</a>
								</li>
							</ul>
						</Dialog.Content>
					</Dialog.Positioner>
				</Portal>
			</Dialog>
		</AppBar.Trail>
	</AppBar.Toolbar>
</AppBar>
