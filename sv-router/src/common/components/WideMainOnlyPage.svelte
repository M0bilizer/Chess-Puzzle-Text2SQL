<script>
	let { children, class: className } = $props();
</script>

<div class={['container', className]}>
	{@render children()}
</div>

<style>
	.container {
		display: flex;
		justify-content: center;
		width: 100%;
		min-height: calc(100vh - 77px - 60px);
		overflow: hidden;
		margin: 0 auto;
	}

	.container > :global(main) {
		max-width: 1200px;
		width: 100%;
		min-height: 100%;
	}
</style>
