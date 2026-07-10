import { PersistedState } from 'runed';
import type { Playlist } from './current-playlist.svelte';

export type PlaylistCollection = {
  playlists: Record<string, Playlist>
  active: string | null;
}

class PlaylistCollectionStore {
  private state = new PersistedState<PlaylistCollection>('playlist-collection', { playlists: {} as Record<string, Playlist>, active: null });

  public all = $derived(this.state.current.playlists)
  public active = $derived(this.state.current.playlists[this.state.current.active || ""] || null)

  public getByName(name: string) {
    return this.state.current.playlists[name]
  }

  public set(playlist: Playlist) {
    this.state.current.playlists[playlist.name] = playlist
  }

  public remove(name: string) {
    delete this.state.current.playlists[name]
  }

  public setActive(name: string | null) {
    this.state.current.active = name
  }
}

export const playlistCollection = new PlaylistCollectionStore();
