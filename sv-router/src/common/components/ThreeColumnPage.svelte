<script>
	let { left, children, right, class: className } = $props();
</script>

<div class={['three-col', className]}>
	<aside>
		{@render left?.()}
	</aside>
	<main>
		{@render children()}
	</main>
	<aside>
		{@render right?.()}
	</aside>
</div>

<style>
	.three-col {
		display: grid;
		width: 100%;
		height: 100%;
		grid-template-columns: 1fr 3fr 1fr;
	}
</style>
