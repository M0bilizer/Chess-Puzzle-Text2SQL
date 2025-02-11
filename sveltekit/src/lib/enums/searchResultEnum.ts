export enum SearchResultEnum {
	Success = 'success',
	BackendError = 'backendError',
	ConnectionError = 'connectionError',
	ConfigurationError = 'configurationError',
	PostError = 'postError',
	UnknownServerError = 'unknownServerError',
	UnknownServerStatusError = 'unknownServerStatusError',
	UnknownClientError = 'unknownClientError'
}

const standardErrors = new Set<SearchResultEnum>([
	SearchResultEnum.ConnectionError,
	SearchResultEnum.ConfigurationError,
	SearchResultEnum.PostError,
	SearchResultEnum.UnknownServerError
]);
export function isStandardError(searchResultEnum: SearchResultEnum) {
	return standardErrors.has(searchResultEnum);
}
