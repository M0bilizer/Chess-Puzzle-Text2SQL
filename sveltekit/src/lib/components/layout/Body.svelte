<script lang="ts">
	let { children } = $props();
</script>

<div class="container mx-auto grid grid-cols-1 xl:grid-cols-[200px_minmax(0px,_1fr)_290px]">
	<aside class="left">aside</aside>
	<main id="_top" class="space-y-8 self-start p-4 xl:p-10 xl:pb-48">
		{@render children()}
	</main>
	<aside class="right">right</aside>
</div>

<style>
	.left {
		position: sticky;
		top: 0;
		display: none;
		max-height: 100vh;
		align-self: flex-start;
		overflow-y: auto;
		padding: 2.5rem 1rem 24rem;
	}

	@media (min-width: 1280px) {
		.left {
			display: block;
			padding-left: 0;
			padding-right: 0;
		}
	}

	.right {
		position: sticky;
		top: 0;
		display: none;
		max-height: 100vh;
		align-self: flex-start;
		overflow-y: auto;
		padding: 1rem;
	}

	@media (min-width: 1280px) {
		.right {
			display: block;
			padding: 2.5rem;
			padding-left: 0;
		}
	}
</style>
