<script lang="ts">
	import { navigate } from '@/router';
	import { playlistCollection } from '../store/playlist-collection.svelte';
	import { currentPlaylist } from '../store/current-playlist.svelte';

	type Props = {
		class?: string;
	};

	let { class: className = '' }: Props = $props();


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

<section class={`px-12 ${className}`}>
	<p class="text-lg font-bold text-tertiary-800-200">Recent Collections:</p>
	<ul class="list-inside list-disc space-y-2">
		{#each recentPlaylists as playlist (playlist.name)}
		    {@const total = playlist.puzzles.length}
			{@const completed = playlist.puzzles.filter(p => p.result).length}
			<li>
				<button class="btn" onclick={() => handleRecentPlaylistClick(playlist.name)}>
				    <h2 class="">{playlist.name}</h2>
					<p class="text-sm text-tertiary-800-200">({completed} / {total} solved)</p>
				</button>
			</li>
		{/each}
	</ul>
</section>
