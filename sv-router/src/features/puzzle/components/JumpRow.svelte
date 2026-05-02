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
	<button class="btn" onclick={onReset} disabled={!canGoBack}>Reset</button>
	<button class="btn" onclick={onBack} disabled={!canGoBack}>Back</button>
	<button class="btn" onclick={onForward} disabled={!canGoForward}>Forward</button>
	<button class="btn" onclick={onEnd} disabled={!canGoForward}>End</button>
</div>
