import type { Puzzle } from '$lib/types/puzzle';
import { KOTLIN_SPRING_URL } from '$env/static/private';
import { json, type RequestHandler } from '@sveltejs/kit';

class Success {
	public readonly data: Puzzle[];

	constructor(data: Puzzle[]) {
		this.data = data;
	}
}

class BackendFailure {
	public readonly message: string | null;

	constructor(message: string | null = null) {
		this.message = message;
	}
}

class ConnectionFailure {
	public readonly message: 'Cannot connect to server';
	constructor() {
		this.message = 'Cannot connect to server';
	}
}

class UnknownError {
	public readonly message: 'Unknown error';
	constructor() {
		this.message = 'Unknown error';
	}
}

class ConfigurationError {
	public readonly message: 'Configuration error';
	constructor() {
		this.message = 'Configuration error';
	}
}

type Result = Success | BackendFailure | ConnectionFailure | ConfigurationError | UnknownError;

// =============================================================================//

export const POST: RequestHandler = async ({ request }) => {
	const { query } = await request.json();

	const result: Result = await _callBackend(query);

	if (result instanceof Success) {
		return json({ status: 'success', data: result.data });
	} else if (result instanceof ConnectionFailure) {
		return json({ status: 'connectionFailure', message: result.message });
	} else if (result instanceof BackendFailure) {
		return json({ status: 'backendFailure', message: result.message });
	} else if (result instanceof ConfigurationError) {
		return json({ status: 'configurationError', message: result.message });
	} else {
		return json({ status: 'unknownFailure' });
	}
};

async function _callBackend(query: string): Promise<Result> {
	if (!`${KOTLIN_SPRING_URL}` || `${KOTLIN_SPRING_URL}`.length === 0) {
		return new ConfigurationError();
	}
	let res: Response;
	try {
		res = await fetch(`${KOTLIN_SPRING_URL}/queryPuzzle`, {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ query })
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

	const responseData = await res.json();
	if (responseData.status !== 'success') {
		return new BackendFailure(responseData.message || 'Unexpected error from backend');
	}
	return new Success(responseData.data);
}
