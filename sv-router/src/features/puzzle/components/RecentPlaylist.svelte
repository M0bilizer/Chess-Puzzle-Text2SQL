<script lang="ts">
	import { navigate } from '@/router';
	import { playlistCollection } from '../store/playlist-collection.svelte';
	import { currentPlaylist } from '../store/current-playlist.svelte';

	function handleRecentPlaylistClick(name: string) {
		playlistCollection.setActive(name);

		navigate('/puzzle/:id', {
			params: {
				id: currentPlaylist.currentPuzzle!.puzzleId
			}
		});
	}

	const recentPlaylists = $derived.by(() => {
		return Object.values(playlistCollection.all);
	});
</script>

<section class="px-12">
	<p class="text-lg font-bold">Recent Playlist:</p>
	<ul class="list-inside list-disc space-y-2">
		{#each recentPlaylists as playlist (playlist.name)}
			<li>
				<button class="btn hover:underline" onclick={() => handleRecentPlaylistClick(playlist.name)}
					>{playlist.name}</button
				>
			</li>
		{/each}
	</ul>
</section>
