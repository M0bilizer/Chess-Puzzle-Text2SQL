<script>
	let { children, class: className } = $props();
</script>

<main class={['center', className]}>
	{@render children()}
</main>

<style>
	.center {
		display: flex;
		justify-content: center;
		align-items: center;
		width: 100%;
		height: 100%;
	}
</style>
