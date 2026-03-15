<script>
	import TablerSearch from '~icons/tabler/search';
	import { navigate, p, ROUTES } from '@/router.ts';
	import CenteredPage from '@/common/components/CenteredPage.svelte';

	let searchQuery = '';
</script>

<CenteredPage>
	<div class="input-group flex w-4/5 flex-row">
		<input
			class="ig-input px-12 text-2xl break-normal"
			type="search"
			placeholder="Find Dutch Defense Puzzle..."
			bind:value={searchQuery}
		/>
		<button
			onclick={() => navigate(p(ROUTES.SEARCH), { search: { q: searchQuery } })}
			class="ig-btn inline-flex items-center gap-2 preset-filled px-12 py-8"
		>
			<span class="text-2xl">Search</span>
			<TablerSearch class="size-6" />
		</button>
	</div>
</CenteredPage>
