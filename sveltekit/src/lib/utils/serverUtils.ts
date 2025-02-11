import type { Puzzle } from '$lib/types/puzzle';
import { json } from '@sveltejs/kit';
import { SearchResultEnum } from '../../enums/searchResultEnum';

export interface Result {
	getJson(): Response;
}

export class Success implements Result {
	public readonly data: Puzzle[];

	constructor(data: Puzzle[]) {
		this.data = data;
	}

	getJson() {
		return json({ status: SearchResultEnum.Success, data: this.data });
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
