import { PersistedState } from 'runed';

import type { Collection } from './current-collection.svelte';

type Collections = {
	collections: Record<string, Collection>;
	active: string | null;
};

// make this code more functional to make it cleaner
class CollectionsStore {
	private state = new PersistedState<Collections>('collections', {
		collections: {} as Record<string, Collection>,
		active: null
	});

	public all = $derived(this.state.current.collections);
	public active = $derived(this.state.current.collections[this.state.current.active || ''] || null);

	public getByName(name: string) {
		return this.state.current.collections[name];
	}

	public set(collection: Collection) {
		this.state.current.collections[collection.name] = collection;
	}

	public remove(name: string) {
		delete this.state.current.collections[name];
	}

	public setActive(name: string | null) {
		this.state.current.active = name;
	}
}

export const collections = new CollectionsStore();
