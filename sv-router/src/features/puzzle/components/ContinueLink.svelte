<script lang="ts">
	import ArrowRightIcon from '~icons/tabler/arrow-right';
	import { p } from '@/router';
	import type { Playlist } from '../store/current-playlist.svelte';

	type Props = {
		class?: string;
		playlist: Playlist;
	};
	const { class: className, playlist }: Props = $props();

	const name = $derived(playlist.name);
	const id = $derived(playlist.puzzles[playlist.currentIndex]!.puzzleId);
	const current = $derived(playlist.currentIndex + 1);
	const total = $derived(playlist.puzzles.length);
</script>

<a
	href={p('/puzzle/:id', { params: { id: id } })}
	class="content-center anchor {className} opacity-50 transition-opacity duration-100 hover:opacity-100"
>
	Continue '{name}' — {current} / {total}
	<ArrowRightIcon class="inline" />
</a>
