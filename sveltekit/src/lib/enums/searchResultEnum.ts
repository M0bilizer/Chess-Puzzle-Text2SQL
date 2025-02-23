export enum SearchResultEnum {
	Success = 'success',
	NoResultsError = 'noResultsError',
	BackendError = 'backendError',
	ConnectionError = 'connectionError',
	ConfigurationError = 'configurationError',
	ImplementationError = 'implementationError',
	PostError = 'postError',
	UnknownServerError = 'unknownServerError',
	UnknownServerStatusError = 'unknownServerStatusError',
	UnknownClientError = 'unknownClientError'
}

const standardErrors = new Set<SearchResultEnum>([
	SearchResultEnum.NoResultsError,
	SearchResultEnum.ConnectionError,
	SearchResultEnum.ConfigurationError,
	SearchResultEnum.ImplementationError,
	SearchResultEnum.PostError,
	SearchResultEnum.UnknownServerError
]);
export function isStandardError(searchResultEnum: SearchResultEnum) {
	return standardErrors.has(searchResultEnum);
}
