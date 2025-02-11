<script lang="ts">
	import RecentSearches from '$lib/components/ui/RecentSearches.svelte';
	import QueryDisplay from '$lib/components/ui/QueryDisplay.svelte';
</script>

<div class="grid grid-rows-[auto_auto] gap-4">
	<QueryDisplay />
	<RecentSearches />
</div>
