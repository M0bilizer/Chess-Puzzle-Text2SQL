import type { BaseIssue } from 'valibot';

export class IOError extends Error {
	readonly type = 'io-error';
	readonly message =
		'The request took too long to complete. Please check your internet connection and try again.';
}

export class ConfigurationError extends Error {
	readonly type = 'configuration-error';
	readonly message = 'The configuration is not valid.';
	public readonly issues?: BaseIssue<unknown>[];

	constructor(issues: BaseIssue<unknown>[]) {
		super('The configuration is not valid.'); // Pass message to Error constructor
		this.name = 'ConfigurationError';
		this.issues = issues;
	}

	getFormattedMessages(): string[] {
		if (!this.issues || this.issues.length === 0) {
			return [this.message];
		}

		return this.issues.map((issue) => {
			const path = issue.path?.map((p) => p.key).join('.') || 'config';
			return `[${path}]: ${issue.message}`;
		});
	}

	getIssuesForField(fieldName: string): BaseIssue<unknown>[] {
		return this.issues?.filter((issue) => issue.path?.some((p) => p.key === fieldName)) || [];
	}

	hasErrorForField(fieldName: string): boolean {
		return this.getIssuesForField(fieldName).length > 0;
	}

	toJSON() {
		return {
			name: this.name,
			type: this.type,
			message: this.message,
			issues: this.issues?.map((issue) => ({
				message: issue.message,
				path: issue.path?.map((p) => p.key),
				input: issue.input
			})),
			formattedMessages: this.getFormattedMessages()
		};
	}
}
