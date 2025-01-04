<script lang="ts">
	let { children } = $props();
</script>

<div class="container mx-auto grid grid-cols-1 xl:grid-cols-[200px_minmax(0px,_1fr)_290px]">
	<aside
		class="sticky top-0 hidden max-h-screen self-start overflow-y-auto p-4 py-10 pb-96 xl:block xl:px-0"
	>
		aside
	</aside>
	<main id="_top" class="space-y-8 self-start p-4 xl:p-10 xl:pb-48">
		{@render children()}
	</main>
	<aside
		class="sticky top-0 hidden max-h-screen self-start overflow-y-auto p-4 xl:block xl:p-10 xl:pl-0"
	>
		right
	</aside>
</div>
