<script lang="ts">
	import type { Snippet } from 'svelte';

	type Props = {
		value: string;
		children: Snippet<[{ copied: boolean; copy: () => void }]>;
	};
	let { value, children }: Props = $props();

	let copied = $state(false);
	let timer: ReturnType<typeof setTimeout> | null = $state(null);

	function copy() {
		navigator.clipboard.writeText(value);

		if (timer) clearTimeout(timer);

		copied = true;
		timer = setTimeout(() => {
			copied = false;
		}, 3000);
	}
</script>

{@render children({ copied, copy })}
