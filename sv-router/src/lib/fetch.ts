import ky from 'ky';
import type { BeforeRequestHook, AfterResponseHook } from 'ky';
import type { AppCache } from './cache';

export interface HttpClient {
	get<T>(
		url: string,
		options?: {
			headers?: Record<string, string>;
			context?: Record<string, unknown>;
		}
	): Promise<T>;
}

export function createCacheClient(
	baseUrl: string,
	cache: AppCache,
	defaultTTL: number = 300000
): HttpClient {
	const beforeRequest: BeforeRequestHook = async (request) => {
		const cacheKey = request.options.context?.cacheKey as string;
		const skipCache = request.options.context?.skipCache as boolean;

		if (!cacheKey || skipCache) {
			return;
		}

		const cachedData = await cache.get(cacheKey);

		if (cachedData) {
			console.log('cache hit!');
			return new Response(JSON.stringify(cachedData), {
				status: 200,
				headers: {
					'Content-Type': 'application/json',
					'X-Cache': 'HIT'
				}
			});
		}
		return;
	};

	const afterResponse: AfterResponseHook = async (state) => {
		const cacheKey = state.options.context?.cacheKey as string;
		const cacheTTL = state.options.context?.cacheTTL as number;
		const response = state.response;

		if (cacheKey && response.ok && !response.headers.get('X-Cache')) {
			const data = await response.clone().json();
			await cache.set(cacheKey, data, cacheTTL ?? defaultTTL);
		}

		return response;
	};

	const kyInstance = ky.extend({
		hooks: {
			beforeRequest: [beforeRequest],
			afterResponse: [afterResponse]
		},
		baseUrl: baseUrl
	});

	return {
		get: async <T>(
			url: string,
			options?: {
				headers?: Record<string, string>;
				context?: Record<string, unknown>;
			}
		): Promise<T> => {
			return kyInstance.get(url, options).json<T>();
		}
	};
}
