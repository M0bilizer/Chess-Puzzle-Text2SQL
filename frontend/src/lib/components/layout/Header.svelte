<script lang="ts">
	import Title from '$lib/components/ui/Title.svelte';
	import DebugModal from '$lib/components/modals/DebugModal.svelte';
	import SearchBox from '$lib/components/ui/SearchBox.svelte';
</script>

<header class="w-full border-b-[1px] border-surface-500/20 p-4 py-3 bg-surface-50-950 sm:px-10">
	<div
		class="container mx-auto grid max-w-screen-2xl grid-cols-[auto_1fr_auto] items-center gap-4 sm:grid-cols-[1fr_auto_1fr]"
	>
		<div class="left"><Title /></div>
		<div class="middle"><SearchBox /></div>
		<div class="right">
			<DebugModal />
		</div>
	</div>
</header>

<style>
	.left {
		display: flex;
		align-items: center;
		justify-content: flex-start;
		gap: 1.5rem;
	}

	.middle {
		display: flex;
		align-items: center;
		gap: 0.5rem;
	}

	.right {
		display: flex;
		align-items: center;
		justify-content: end;
		gap: 0.5rem;
	}
</style>
