import { clear, del, get, set, setMany } from 'idb-keyval';

type CacheEntry<T> = {
	data: T;
	expiresAt: number;
	timestamp: number;
};

export interface AppCache {
	set<T>(key: string, data: T, ttl?: number): Promise<void>;
	get<T>(key: string): Promise<T | null>;
	setMany<T>(items: Array<{ key: string; data: T; ttl?: number }>): Promise<void>;
	delete(key: string): Promise<void>;
	clear(): Promise<void>;
}

export class IndexedDbCache implements AppCache {
	private defaultTTL: number;

	constructor(defaultTTL: number = 5 * 60 * 1000) {
		this.defaultTTL = defaultTTL;
	}

	async set<T>(key: string, data: T, ttl?: number): Promise<void> {
		const entry: CacheEntry<T> = {
			data,
			expiresAt: Date.now() + (ttl ?? this.defaultTTL),
			timestamp: Date.now()
		};
		await set(key, entry);
	}

	async get<T>(key: string): Promise<T | null> {
		const entry = await get<CacheEntry<T>>(key);

		if (!entry) return null;
		if (Date.now() > entry.expiresAt) {
			await this.delete(key);
			return null;
		}

		return entry.data;
	}

	async setMany<T>(items: Array<{ key: string; data: T; ttl?: number }>): Promise<void> {
		const entries: Array<[string, CacheEntry<T>]> = items.map((item) => [
			item.key,
			{
				data: item.data,
				expiresAt: Date.now() + (item.ttl ?? this.defaultTTL),
				timestamp: Date.now()
			}
		]);
		await setMany(entries);
	}

	async delete(key: string): Promise<void> {
		await del(key);
	}

	async clear(): Promise<void> {
		await clear();
	}
}
