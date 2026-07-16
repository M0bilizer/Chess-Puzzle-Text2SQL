<script lang="ts">
	import { navigate } from '@/router';
	import ArrowRightIcon from '~icons/tabler/arrow-right';
	import type { Playlist } from '../store/current-playlist.svelte';
	import { playlistCollection } from '../store/playlist-collection.svelte';

	type Props = {
		class?: string;
		playlist: Playlist;
	};
	const { class: className, playlist }: Props = $props();

	const name = $derived(playlist.name);
	const id = $derived(playlist.puzzles[playlist.currentIndex]!.puzzleId);
	const current = $derived(playlist.currentIndex);
	const total = $derived(playlist.puzzles.length);

	const onClick = (playlist: Playlist) => {
		playlistCollection.setActive(playlist.name);
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
