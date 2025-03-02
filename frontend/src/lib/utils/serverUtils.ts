import type { Puzzle } from '$lib/types/puzzle';
import { json } from '@sveltejs/kit';
import { SearchResultEnum } from '$lib/enums/searchResultEnum';
import type { SearchMetadata } from '$lib/types/SearchMetadata';

export interface Result {
	getJson(): Response;
}

export class Success implements Result {
	public readonly metadata: SearchMetadata;
	public readonly data: Puzzle[];

	constructor(metadata: SearchMetadata, data: Puzzle[]) {
		this.metadata = metadata;
		this.data = data;
	}

	getJson() {
		return json({ status: SearchResultEnum.Success, metadata: this.metadata, data: this.data });
	}
}

export class NullQueryFailure implements Result {
	public readonly message: 'Query should not be null';

	constructor() {
		this.message = 'Query should not be null';
	}

	getJson() {
		return json({ status: SearchResultEnum.NullQueryError, message: this.message });
	}
}

export class NoResultsFailure implements Result {
	public readonly message: 'No results found';

	constructor() {
		this.message = 'No results found';
	}

	getJson() {
		return json({ status: SearchResultEnum.NoResultsError, message: this.message });
	}
}

export class BackendFailure implements Result {
	public readonly message: string | null;

	constructor(message: string | null = null) {
		this.message = message;
	}

	getJson() {
		return json({ status: SearchResultEnum.BackendError, message: this.message });
	}
}

export class ConnectionFailure implements Result {
	public readonly message: 'Cannot connect to server';

	constructor() {
		this.message = 'Cannot connect to server';
	}

	getJson() {
		return json({ status: SearchResultEnum.ConnectionError, message: this.message });
	}
}

export class UnknownError implements Result {
	public readonly message: 'Unknown error';

	constructor() {
		this.message = 'Unknown error';
	}

	getJson() {
		return json({ status: SearchResultEnum.UnknownServerError, message: this.message });
	}
}

export class ConfigurationError implements Result {
	public readonly message: 'Configuration error';

	constructor() {
		this.message = 'Configuration error';
	}

	getJson() {
		return json({ status: SearchResultEnum.ConfigurationError, message: this.message });
	}
}

export class ImplementationError implements Result {
	public readonly message: 'Implementation error';

	constructor() {
		this.message = 'Implementation error';
	}

	getJson() {
		return json({ status: SearchResultEnum.ImplementationError, message: this.message });
	}
}
