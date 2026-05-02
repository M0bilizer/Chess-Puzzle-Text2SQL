<script lang="ts">
	type Props = {
		onReset: () => void;
		onBack: () => void;
		onForward: () => void;
		onEnd: () => void;
		canGoBack: boolean;
		canGoForward: boolean;
	};
	let { onReset, onBack, onForward, onEnd, canGoBack, canGoForward }: Props = $props();
</script>

<div>
	<button onclick={onReset} disabled={!canGoBack}>Reset</button>
	<button onclick={onBack} disabled={!canGoBack}>Back</button>
	<button onclick={onForward} disabled={!canGoForward}>Forward</button>
	<button onclick={onEnd} disabled={!canGoForward}>End</button>
</div>
