import { KOTLIN_SPRING_URL } from '$env/static/private';
import { type RequestHandler } from '@sveltejs/kit';
import {
	BackendFailure,
	ConfigurationError,
	ConnectionFailure,
	type Result,
	Success,
	UnknownError
} from '$lib/utils/serverUtils';
import type { ModelEnum } from '$lib/enums/modelEnum';

export const POST: RequestHandler = async ({ request }) => {
	const { query, model } = await request.json();
	const result = await _callBackend(query, model);
	return result.getJson();
};

async function _callBackend(query: string, model: ModelEnum): Promise<Result> {
	if (!`${KOTLIN_SPRING_URL}` || `${KOTLIN_SPRING_URL}`.length === 0) {
		return new ConfigurationError();
	}
	let res: Response;
	try {
		res = await fetch(`${KOTLIN_SPRING_URL}/queryPuzzle`, {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ query, model })
		});
	} catch (error: unknown) {
		if (error instanceof Error) {
			return new ConnectionFailure();
		} else {
			return new UnknownError();
		}
	}

	if (!res.ok) {
		return new ConnectionFailure();
	}

	//TODO
	const json = await res.json();
	if (json.status !== 'success') {
		return new BackendFailure(json.message || 'Unexpected error from backend');
	}
	return new Success(json.metadata, json.data);
}
