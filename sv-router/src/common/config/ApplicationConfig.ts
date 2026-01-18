import { Result } from 'typescript-result';
import { EnvSchema, type Env } from '../types/env';
import * as v from 'valibot';
import { ConfigurationError } from '../types/error';

export function loadConfigFromVite(): Record<string, string> {
	return {
		baseUrl: import.meta.env.VITE_BASE_URL
	};
}

export function loadApplicationConfiguration(
	viteEnv: Record<string, string>
): Result<Env, ConfigurationError> {
	const result = v.safeParse(EnvSchema, viteEnv);
	if (!result.success) {
		return Result.error(new ConfigurationError(result.issues));
	}
	return Result.ok(result.output);
}
