import { env } from '$env/dynamic/private';
import { type RequestHandler } from '@sveltejs/kit';
import {
	BackendFailure,
	ConfigurationError,
	ConnectionFailure,
	ImplementationError,
	NoResultsFailure,
	type Result,
	Success,
	UnknownError
} from '$lib/utils/serverUtils';
import type { ModelEnum } from '$lib/enums/modelEnum';
import type { Puzzle } from '$lib/types/puzzle';
import type { SearchMetadata } from '$lib/types/SearchMetadata';

const KOTLIN_SPRING_URL = env.KOTLIN_SPRING_URL;
const API = `${KOTLIN_SPRING_URL}/queryPuzzle`;

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
		console.log(`fetching ${API} with query: ${query}, model: ${model}`);
		res = await fetch(`${API}`, {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ query, model })
		});
	} catch (error: unknown) {
		console.error(`└-> Error when calling ${API}: ${error}`);
		if (error instanceof Error) {
			return new ConnectionFailure();
		} else {
			return new UnknownError();
		}
	}

	const BAD_REQUEST = 400;
	if (res.status == BAD_REQUEST) {
		return new ImplementationError();
	}

	if (!res.ok) {
		return new ConnectionFailure();
	}

	type success = {
		status: 'success';
		data: Puzzle[];
		metadata: SearchMetadata;
	};

	type failure = {
		status: 'failure';
		message: string;
	};

	const json: success | failure = await res.json();
	if (json.status !== 'success') {
		return new BackendFailure(json.message || 'Unexpected error from backend');
	}
	if (json.data.length === 0) {
		return new NoResultsFailure();
	}
	return new Success(json.metadata, json.data);
}
