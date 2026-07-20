export class IOError extends Error {
	readonly type = 'io-error';
	readonly message =
		'The request took too long to complete. Please check your internet connection and try again.';
}
