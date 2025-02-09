<script lang="ts">
	import RecentSearches from '$lib/components/layout/RecentSearches.svelte';
	import QueryDisplay from '$lib/components/layout/QueryDisplay.svelte';
</script>

<div class="grid grid-rows-[auto_auto] gap-4">
	<QueryDisplay />
	<RecentSearches />
</div>
