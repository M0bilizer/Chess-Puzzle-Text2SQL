<script>
	let { message, toastId } = $props();
</script>

<div class="card w-full border-[1px] border-surface-200-800 preset-filled-surface-100-900">
	<p>{message} {toastId}</p>
</div>
