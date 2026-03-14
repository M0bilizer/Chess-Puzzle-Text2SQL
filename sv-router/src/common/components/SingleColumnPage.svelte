<script>
	let { children, class: className } = $props();
</script>

<main class={['single-col', className]}>
	{@render children()}
</main>

<style>
	.single-col {
		width: 100%;
		height: 100%;
	}
</style>
