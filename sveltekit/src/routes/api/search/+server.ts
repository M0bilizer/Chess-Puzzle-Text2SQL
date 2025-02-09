import type { Puzzle } from '$lib/types/puzzle';
import { KOTLIN_SPRING_URL } from '$env/static/private';
import { getDataStub } from '$lib/utils/dataStub';
import { json, type RequestHandler } from '@sveltejs/kit';

class Success {
	public readonly data: Puzzle[];

	constructor(data: Puzzle[]) {
		this.data = data;
	}
}

class Failure {
	public readonly message: string | null;

	constructor(message: string | null = null) {
		this.message = message;
	}
}

type Result = Success | Failure;

export const POST: RequestHandler = async ({ request }) => {
	const { query } = await request.json();

	try {
		const result = await _callBackend(query);
		if (result instanceof Failure) {
			const data = getDataStub();
			return json({ status: 'stub', data });
		} else {
			return json({ status: 'success', data: result.data });
		}
	} catch (error) {
		const data = getDataStub();
		return json({ status: 'failure', message: error.message || 'An error occurred', data: data });
	}
};

async function _callBackend(query: string): Promise<Result> {
	const res = await fetch(`${KOTLIN_SPRING_URL}/queryPuzzle`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ query })
	});

	if (!res.ok) {
		return new Failure('Failed to fetch from backend');
	}

	const responseData = await res.json();

	if (responseData.status !== 'success') {
		return new Failure(responseData.message || 'Unexpected error from backend');
	}

	return new Success(responseData.data);
}
