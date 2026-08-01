<script lang="ts">
	import { navigate } from '@/router';
	import ArrowRightIcon from '~icons/tabler/arrow-right';

	import { collections } from '../store/collections.svelte';
	import type { Collection } from '../store/current-collection.svelte';

	type Props = {
		class?: string;
		playlist: Collection;
	};

	const { class: className, playlist = $bindable() }: Props = $props();

	const name = $derived(playlist.name);
	const id = $derived(playlist.puzzles.find((it) => it.result === undefined)!.puzzleId);
	const current = $derived(playlist.puzzles.filter((it) => it.result !== undefined).length);
	const total = $derived(playlist.puzzles.length);

	const onClick = (playlist: Collection) => {
		collections.setActive(playlist.name);
		navigate('/puzzle/:id', { params: { id: id } });
	};
</script>

<button
	onclick={() => onClick(playlist)}
	class="anchor {className} opacity-50 transition-opacity duration-100 hover:opacity-100"
>
	Continue '{name}' — {current} / {total}
	<ArrowRightIcon class="inline" />
</button>
